package com.example.wordassistant.controller;

import com.example.wordassistant.entity.UserProgress;
import com.example.wordassistant.repository.UserProgressRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final UserProgressRepository progressRepository;

    public ProgressController(UserProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    /**
     * 只允许读写自己的学习进度, 拒绝越权访问他人 userId。
     */
    private ResponseEntity<?> checkOwner(Long userId, HttpSession session) {
        Object sessionUserId = session.getAttribute("userId");
        if (sessionUserId == null || !((Long) sessionUserId).equals(userId)) {
            return ResponseEntity.status(403).body(Map.of("message", "只能访问自己的学习进度"));
        }
        return null;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getProgress(@PathVariable Long userId, HttpSession session) {
        ResponseEntity<?> denied = checkOwner(userId, session);
        if (denied != null) return denied;

        List<UserProgress> list = progressRepository.findByUserId(userId);
        Map<String, String> progressMap = new HashMap<>();
        for (UserProgress p : list) {
            progressMap.put(p.getWord(), p.getStatus());
        }
        return ResponseEntity.ok(progressMap);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> syncProgress(@PathVariable Long userId, @RequestBody Map<String, String> payload, HttpSession session) {
        ResponseEntity<?> denied = checkOwner(userId, session);
        if (denied != null) return denied;

        // 全量同步: 逐词 upsert
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
