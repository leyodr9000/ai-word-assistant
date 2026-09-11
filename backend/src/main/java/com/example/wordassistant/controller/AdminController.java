package com.example.wordassistant.controller;

import com.example.wordassistant.entity.SystemConfig;
import com.example.wordassistant.entity.User;
import com.example.wordassistant.entity.VisitLog;
import com.example.wordassistant.repository.SystemConfigRepository;
import com.example.wordassistant.repository.UserRepository;
import com.example.wordassistant.repository.VisitLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private SystemConfigRepository configRepo;

    @Autowired
    private VisitLogRepository visitRepo;
    
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        // Record a visit log when this is called, or you can record it in a filter
        visitRepo.save(new VisitLog());
        
        long visitCount = visitRepo.count();
        Map<String, Object> stats = new HashMap<>();
        stats.put("visitCount", visitCount);
        
        // Simulating CPU and Memory for the project
        double cpuLoad = Math.random() * 5 + 1; // 1% to 6%
        long memoryUsage = (long) (Math.random() * 30 + 30); // 30% to 60%
        stats.put("cpuLoad", String.format("%.1f", cpuLoad));
        stats.put("memoryUsage", memoryUsage);

        return stats;
    }
    
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "用户删除成功"));
    }

    @GetMapping("/config")
    public SystemConfig getConfig() {
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

    @PostMapping("/config")
    public SystemConfig saveConfig(@RequestBody SystemConfig newConfig) {
        newConfig.setId(1L); // We only keep one config record for simplicity
        return configRepo.save(newConfig);
    }
}
