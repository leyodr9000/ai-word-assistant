package com.example.wordassistant.controller;

import com.example.wordassistant.entity.StudyRecord;
import com.example.wordassistant.repository.StudyRecordRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * 学习时长上报接口 (仅登录用户)。
 * 前端采用"增量上报"策略: 每分钟/离开页面时把当日本地累计与已同步值的差值发来,
 * 服务端按 (用户, 日期) 累加, 天然幂等防重, 单日上限 24 小时。
 */
@RestController
@RequestMapping("/api/study-time")
public class StudyTimeController {

    /** 单次上报增量上限: 1 小时 (正常同步周期远小于此, 防异常数据) */
    private static final long MAX_DELTA = 3600;
    private static final long MAX_DAY_SECONDS = 86400;

    private final StudyRecordRepository repo;

    public StudyTimeController(StudyRecordRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/today")
    public ResponseEntity<?> today(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        LocalDate today = LocalDate.now();
        Integer seconds = repo.findByUserIdAndStudyDate(userId, today)
                .map(StudyRecord::getSeconds)
                .orElse(0);
        return ResponseEntity.ok(Map.of("date", today.toString(), "seconds", seconds));
    }

    @GetMapping("/total")
    public Map<String, Object> total(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return Map.of("totalSeconds", repo.sumSecondsByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody Map<String, Object> body, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        String dateStr = body.get("date") == null ? "" : String.valueOf(body.get("date"));
        Number deltaNum = (Number) body.get("delta");
        if (deltaNum == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "缺少 delta"));
        }
        long delta = deltaNum.longValue();
        if (delta <= 0 || delta > MAX_DELTA) {
            return ResponseEntity.badRequest().body(Map.of("message", "delta 取值不合法"));
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "日期格式不合法, 应为 yyyy-MM-dd"));
        }
        if (date.isAfter(LocalDate.now())) {
            return ResponseEntity.badRequest().body(Map.of("message", "不能上报未来日期"));
        }

        StudyRecord record = repo.findByUserIdAndStudyDate(userId, date).orElseGet(() -> {
            StudyRecord n = new StudyRecord();
            n.setUserId(userId);
            n.setStudyDate(date);
            n.setSeconds(0);
            return n;
        });
        record.setSeconds((int) Math.min(MAX_DAY_SECONDS, record.getSeconds() + delta));
        repo.save(record);

        return ResponseEntity.ok(Map.of("date", date.toString(), "seconds", record.getSeconds()));
    }
}
