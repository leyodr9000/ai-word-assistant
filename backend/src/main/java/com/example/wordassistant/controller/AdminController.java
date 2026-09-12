package com.example.wordassistant.controller;

import com.example.wordassistant.entity.SystemConfig;
import com.example.wordassistant.entity.User;
import com.example.wordassistant.entity.VisitLog;
import com.example.wordassistant.repository.SystemConfigRepository;
import com.example.wordassistant.repository.UserProgressRepository;
import com.example.wordassistant.repository.UserRepository;
import com.example.wordassistant.repository.VisitLogRepository;
import com.example.wordassistant.repository.WordBookRepository;
import com.example.wordassistant.repository.WordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final SystemConfigRepository configRepo;
    private final VisitLogRepository visitRepo;
    private final UserRepository userRepo;
    private final WordBookRepository bookRepo;
    private final WordRepository wordRepo;
    private final UserProgressRepository progressRepo;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public AdminController(SystemConfigRepository configRepo, VisitLogRepository visitRepo,
                           UserRepository userRepo, WordBookRepository bookRepo,
                           WordRepository wordRepo, UserProgressRepository progressRepo,
                           ObjectMapper objectMapper) {
        this.configRepo = configRepo;
        this.visitRepo = visitRepo;
        this.userRepo = userRepo;
        this.bookRepo = bookRepo;
        this.wordRepo = wordRepo;
        this.progressRepo = progressRepo;
        this.objectMapper = objectMapper;
    }

    /**
     * 真实统计数据: 用户/词书/单词/学习记录/登录次数 + AI 配置状态。
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userRepo.count());
        stats.put("bookCount", bookRepo.count());
        stats.put("cloudWordCount", wordRepo.count());
        stats.put("progressCount", progressRepo.count());
        stats.put("visitCount", visitRepo.count());

        SystemConfig c = configRepo.findById(1L).orElse(null);
        boolean aiConfigured = c != null && c.getApiKey() != null && !c.getApiKey().isBlank();
        stats.put("aiConfigured", aiConfigured);
        stats.put("modelName", c != null ? c.getModelName() : null);
        return stats;
    }

    /** 最近 10 次登录记录 */
    @GetMapping("/visits")
    public List<VisitLog> recentVisits() {
        return visitRepo.findTop10ByOrderByVisitTimeDesc();
    }

    @GetMapping("/users")
    public List<Map<String, Object>> getAllUsers() {
        // User.getPassword 已标注 @JsonIgnore, 密码不会出现在响应中; 附加每用户学习进度数
        List<User> users = userRepo.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (User u : users) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", u.getId());
            item.put("username", u.getUsername());
            item.put("role", u.getRole());
            item.put("createdAt", u.getCreatedAt());
            item.put("progressCount", progressRepo.countByUserId(u.getId()));
            result.add(item);
        }
        return result;
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "用户删除成功"));
    }

    /**
     * 获取 AI 配置。真实 apiKey 永不出后端, 只返回脱敏提示。
     */
    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        SystemConfig c = loadConfigEntity();
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", c.getId());
        dto.put("activeModel", c.getActiveModel());
        dto.put("baseUrl", c.getBaseUrl());
        dto.put("modelName", c.getModelName());
        dto.put("temperature", c.getTemperature());
        String key = c.getApiKey();
        boolean hasKey = key != null && !key.isBlank();
        dto.put("hasApiKey", hasKey);
        dto.put("apiKeyMasked", hasKey ? "••••" + key.substring(Math.max(0, key.length() - 4)) : "");
        dto.put("apiKey", "");
        return dto;
    }

    /**
     * 保存 AI 配置。apiKey 为空或仍是掩码时保留数据库中的旧 Key。
     */
    @PostMapping("/config")
    public SystemConfig saveConfig(@RequestBody SystemConfig newConfig) {
        SystemConfig existing = loadConfigEntity();
        existing.setActiveModel(newConfig.getActiveModel());
        existing.setBaseUrl(newConfig.getBaseUrl());
        existing.setModelName(newConfig.getModelName());
        existing.setTemperature(newConfig.getTemperature());

        String incoming = newConfig.getApiKey();
        if (incoming != null && !incoming.isBlank() && !incoming.startsWith("••")) {
            existing.setApiKey(incoming.trim());
        }
        return configRepo.save(existing);
    }

    /**
     * 连通性测试: apiKey 留空时使用数据库已保存的 Key,
     * 浏览器端永远接触不到真实 Key。
     */
    @PostMapping("/config/test")
    public Map<String, Object> testConfig(@RequestBody Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();
        SystemConfig stored = loadConfigEntity();

        String baseUrl = body.getOrDefault("baseUrl", stored.getBaseUrl());
        String modelName = body.getOrDefault("modelName", stored.getModelName());
        String apiKey = body.get("apiKey");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = stored.getApiKey();
        }

        if (baseUrl == null || baseUrl.isBlank() || modelName == null || modelName.isBlank()) {
            result.put("success", false);
            result.put("message", "Base URL 和模型名称不能为空");
            return result;
        }
        if (apiKey == null || apiKey.isBlank()) {
            result.put("success", false);
            result.put("message", "尚未保存 API Key, 请先填写并保存");
            return result;
        }

        try {
            String endpoint = baseUrl.endsWith("/")
                    ? baseUrl + "chat/completions"
                    : baseUrl + "/chat/completions";

            Map<String, Object> reqBody = Map.of(
                    "model", modelName,
                    "messages", List.of(Map.of("role", "user", "content", "测试连通性, 请回复OK")),
                    "max_tokens", 10
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(reqBody)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                result.put("success", true);
                result.put("message", "连接成功!模型响应正常。");
            } else {
                String detail = "";
                try {
                    detail = objectMapper.readTree(response.body()).path("error").path("message").asText("");
                } catch (Exception ignored) {
                }
                result.put("success", false);
                result.put("message", "连接失败: HTTP " + response.statusCode() + (detail.isEmpty() ? "" : " " + detail));
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "请求出错: " + e.getMessage());
        }
        return result;
    }

    private SystemConfig loadConfigEntity() {
        return configRepo.findById(1L).orElseGet(() -> {
            SystemConfig defaultConfig = new SystemConfig();
            defaultConfig.setId(1L);
            defaultConfig.setActiveModel("DeepSeek");
            defaultConfig.setApiKey("");
            defaultConfig.setBaseUrl("https://api.deepseek.com");
            defaultConfig.setModelName("deepseek-chat");
            defaultConfig.setTemperature(0.7);
            return configRepo.save(defaultConfig);
        });
    }
}
