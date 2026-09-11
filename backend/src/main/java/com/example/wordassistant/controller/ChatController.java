package com.example.wordassistant.controller;

import com.example.wordassistant.entity.SystemConfig;
import com.example.wordassistant.repository.SystemConfigRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Autowired
    private SystemConfigRepository configRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "");

        SystemConfig config = configRepo.findById(1L).orElse(new SystemConfig());
        String baseUrl = config.getBaseUrl();
        String apiKey = config.getApiKey();
        String modelName = config.getModelName() != null && !config.getModelName().isEmpty() ? config.getModelName() : "deepseek-chat";
        Double temperature = config.getTemperature() != null ? config.getTemperature() : 0.7;

        SseEmitter emitter = new SseEmitter(120000L);

        executor.execute(() -> {
            try {
                if (baseUrl == null || baseUrl.isEmpty()) {
                    emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString("请先在后台配置模型 API 地址。")));
                    emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                    emitter.complete();
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

                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();

                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(endpoint))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + (apiKey != null ? apiKey : ""))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                        .thenAccept(response -> {
                            if (response.statusCode() != 200) {
                                try {
                                    String errorMsg = " API 请求失败 (HTTP " + response.statusCode() + ")";
                                    emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(errorMsg)));
                                    emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                                    emitter.complete();
                                } catch (Exception e) {}
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
                                        // Ignore parse errors for broken lines
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
                            try {
                                emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString("网络异常：" + ex.getMessage())));
                                emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                                emitter.completeWithError(ex);
                            } catch (Exception e) {
                            }
                            return null;
                        });
            } catch (Exception e) {
                try {
                    emitter.completeWithError(e);
                } catch (Exception ex) {}
            }
        });

        return emitter;
    }
}
