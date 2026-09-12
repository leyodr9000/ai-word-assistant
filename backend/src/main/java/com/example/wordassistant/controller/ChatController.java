package com.example.wordassistant.controller;

import com.example.wordassistant.entity.SystemConfig;
import com.example.wordassistant.repository.SystemConfigRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    // 复用同一个 HttpClient 与有界线程池, 避免每请求新建/线程无限增长
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final int MAX_MESSAGE_LENGTH = 2000;

    private final ExecutorService executor = Executors.newFixedThreadPool(16);
    private final SystemConfigRepository configRepo;
    private final ObjectMapper objectMapper;

    public ChatController(SystemConfigRepository configRepo, ObjectMapper objectMapper) {
        this.configRepo = configRepo;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "");
        SseEmitter emitter = new SseEmitter(120000L);
        // 客户端断开/超时时清理, 防止连接悬挂
        emitter.onTimeout(emitter::complete);
        emitter.onError(t -> { /* 连接已断, 无需处理 */ });

        if (message.length() > MAX_MESSAGE_LENGTH) {
            return fail(emitter, "消息过长, 请控制在 " + MAX_MESSAGE_LENGTH + " 字以内。");
        }

        SystemConfig config = configRepo.findById(1L).orElse(new SystemConfig());
        String baseUrl = config.getBaseUrl();
        String apiKey = config.getApiKey();
        String modelName = config.getModelName() != null && !config.getModelName().isEmpty() ? config.getModelName() : "deepseek-chat";
        Double temperature = config.getTemperature() != null ? config.getTemperature() : 0.7;

        executor.execute(() -> {
            try {
                if (baseUrl == null || baseUrl.isEmpty()) {
                    fail(emitter, "请先在后台配置模型 API 地址。");
                    return;
                }

                String endpoint = baseUrl;
                if (!endpoint.endsWith("/chat/completions")) {
                    endpoint = endpoint.endsWith("/") ? endpoint + "chat/completions" : endpoint + "/chat/completions";
                }

                String prompt = "你是一个专业的英语助教。请用生动有趣的中文回答学生的问题，并提供相应的英文例句。";

                Map<String, Object> requestBody = Map.of(
                        "model", modelName,
                        "messages", new Object[]{
                                Map.of("role", "system", "content", prompt),
                                Map.of("role", "user", "content", message)
                        },
                        "stream", true,
                        "temperature", temperature
                );

                String jsonBody = objectMapper.writeValueAsString(requestBody);

                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(endpoint))
                        .timeout(Duration.ofSeconds(110))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + (apiKey != null ? apiKey : ""))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                CLIENT.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                        .thenAccept(response -> {
                            if (response.statusCode() != 200) {
                                fail(emitter, "API 请求失败 (HTTP " + response.statusCode() + ")");
                                return;
                            }

                            response.body().forEach(line -> {
                                if (line.startsWith("data: ") && !line.equals("data: [DONE]")) {
                                    String data = line.substring(6);
                                    try {
                                        JsonNode root = objectMapper.readTree(data);
                                        JsonNode contentNode = root.path("choices").get(0).path("delta").path("content");
                                        if (!contentNode.isMissingNode() && !contentNode.isNull()) {
                                            String content = contentNode.asText();
                                            emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(content)));
                                        }
                                    } catch (Exception e) {
                                        // 忽略解析失败的行
                                    }
                                }
                            });
                            try {
                                emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                                emitter.complete();
                            } catch (Exception e) {
                                emitter.completeWithError(e);
                            }
                        })
                        .exceptionally(ex -> {
                            fail(emitter, "网络异常：" + ex.getMessage());
                            return null;
                        });
            } catch (Exception e) {
                try {
                    emitter.completeWithError(e);
                } catch (Exception ex) {
                    // ignore
                }
            }
        });

        return emitter;
    }

    private SseEmitter fail(SseEmitter emitter, String message) {
        try {
            emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(message)));
            emitter.send(SseEmitter.event().name("done").data("[DONE]"));
            emitter.complete();
        } catch (Exception ignored) {
        }
        return emitter;
    }
}
