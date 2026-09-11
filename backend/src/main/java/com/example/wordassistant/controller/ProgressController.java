package com.example.wordassistant.controller;

import com.example.wordassistant.entity.UserProgress;
import com.example.wordassistant.repository.UserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    @Autowired
    private UserProgressRepository progressRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getProgress(@PathVariable Long userId) {
        List<UserProgress> list = progressRepository.findByUserId(userId);
        Map<String, String> progressMap = new HashMap<>();
        for (UserProgress p : list) {
            progressMap.put(p.getWord(), p.getStatus());
        }
        return ResponseEntity.ok(progressMap);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> syncProgress(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        // Simple sync: update or create based on payload map of word -> status
        for (Map.Entry<String, String> entry : payload.entrySet()) {
            String word = entry.getKey();
            String status = entry.getValue();

            Optional<UserProgress> opt = progressRepository.findByUserIdAndWord(userId, word);
            if (opt.isPresent()) {
                UserProgress p = opt.get();
                if (!p.getStatus().equals(status)) {
                    p.setStatus(status);
                    progressRepository.save(p);
                }
            } else {
                UserProgress p = new UserProgress();
                p.setUserId(userId);
                p.setWord(word);
                p.setStatus(status);
                progressRepository.save(p);
            }
        }
        return ResponseEntity.ok(Map.of("message", "进度同步成功"));
    }
}
