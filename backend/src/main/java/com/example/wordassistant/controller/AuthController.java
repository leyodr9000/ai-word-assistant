package com.example.wordassistant.controller;

import com.example.wordassistant.entity.User;
import com.example.wordassistant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "用户名或密码不能为空"));
        }

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "用户名已存在"));
        }

        User user = new User();
        user.setUsername(username);
        // Note: In a real app, hash the password (e.g., BCrypt). For demo, saving plain text.
        user.setPassword(password);
        
        // Auto-assign ADMIN role to 'admin' user, otherwise 'USER'
        if ("admin".equalsIgnoreCase(username)) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }
        
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "注册成功", "user", Map.of("id", user.getId(), "username", user.getUsername(), "role", user.getRole())));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            return ResponseEntity.status(401).body(Map.of("message", "用户名或密码错误"));
        }

        User user = userOpt.get();
        
        // Auto-fix role if missing
        if (user.getRole() == null) {
            user.setRole("admin".equalsIgnoreCase(user.getUsername()) ? "ADMIN" : "USER");
            userRepository.save(user);
        }
        
        return ResponseEntity.ok(Map.of("message", "登录成功", "user", Map.of("id", user.getId(), "username", user.getUsername(), "role", user.getRole())));
    }
}
