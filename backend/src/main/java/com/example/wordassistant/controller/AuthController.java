package com.example.wordassistant.controller;

import com.example.wordassistant.entity.User;
import com.example.wordassistant.entity.VisitLog;
import com.example.wordassistant.repository.UserRepository;
import com.example.wordassistant.repository.VisitLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final VisitLogRepository visitLogRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, VisitLogRepository visitLogRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.visitLogRepository = visitLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        String username = payload.get("username");
        String password = payload.get("password");

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "用户名或密码不能为空"));
        }
        username = username.trim();
        // admin 为系统内置保留账号, 禁止注册抢占
        if ("admin".equalsIgnoreCase(username)) {
            return ResponseEntity.badRequest().body(Map.of("message", "该用户名为系统保留 (内置管理员), 请更换用户名"));
        }
        if (username.length() < 2 || username.length() > 32) {
            return ResponseEntity.badRequest().body(Map.of("message", "用户名长度需在 2-32 个字符之间"));
        }
        if (password.length() < 6 || password.length() > 64) {
            return ResponseEntity.badRequest().body(Map.of("message", "密码长度需在 6-64 个字符之间"));
        }

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "用户名已存在"));
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");

        userRepository.save(user);

        // 注册即登录
        request.getSession().setAttribute("userId", user.getId());
        request.getSession().setAttribute("role", user.getRole());

        return ResponseEntity.ok(Map.of("message", "注册成功", "user", Map.of("id", user.getId(), "username", user.getUsername(), "role", user.getRole())));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        String username = payload.get("username");
        String password = payload.get("password");

        if (username == null || password == null) {
            return ResponseEntity.status(401).body(Map.of("message", "用户名或密码错误"));
        }

        Optional<User> userOpt = userRepository.findByUsername(username.trim());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "用户名或密码错误"));
        }

        User user = userOpt.get();
        String stored = user.getPassword();
        boolean matched;
        if (stored != null && (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$"))) {
            // BCrypt 哈希校验
            matched = passwordEncoder.matches(password, stored);
        } else {
            // 兼容历史明文密码: 校验通过后自动升级为 BCrypt
            matched = stored != null && stored.equals(password);
            if (matched) {
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
            }
        }
        if (!matched) {
            return ResponseEntity.status(401).body(Map.of("message", "用户名或密码错误"));
        }

        // 兼容历史数据缺失角色的情况
        if (user.getRole() == null) {
            user.setRole("admin".equalsIgnoreCase(user.getUsername()) ? "ADMIN" : "USER");
            userRepository.save(user);
        }

        request.getSession().setAttribute("userId", user.getId());
        request.getSession().setAttribute("role", user.getRole());

        // 记录一次登录访问 (用于后台统计)
        visitLogRepository.save(new VisitLog());

        return ResponseEntity.ok(Map.of("message", "登录成功", "user", Map.of("id", user.getId(), "username", user.getUsername(), "role", user.getRole())));
    }

    /**
     * 修改当前登录用户的密码 (需提供正确的当前密码)。
     */
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        Object sessionUserId = request.getSession().getAttribute("userId");
        if (sessionUserId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "未登录"));
        }

        String oldPassword = payload.get("oldPassword");
        String newPassword = payload.get("newPassword");
        if (oldPassword == null || oldPassword.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "请输入当前密码"));
        }
        if (newPassword == null || newPassword.length() < 6 || newPassword.length() > 64) {
            return ResponseEntity.badRequest().body(Map.of("message", "新密码长度需在 6-64 个字符之间"));
        }

        User user = userRepository.findById((Long) sessionUserId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "用户不存在"));
        }

        String stored = user.getPassword();
        boolean oldMatched;
        if (stored != null && (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$"))) {
            oldMatched = passwordEncoder.matches(oldPassword, stored);
        } else {
            oldMatched = stored != null && stored.equals(oldPassword);
        }
        if (!oldMatched) {
            return ResponseEntity.badRequest().body(Map.of("message", "当前密码不正确"));
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "密码修改成功"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok(Map.of("message", "已退出登录"));
    }
}
