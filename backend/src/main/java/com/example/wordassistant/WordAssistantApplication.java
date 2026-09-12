package com.example.wordassistant;

import com.example.wordassistant.entity.User;
import com.example.wordassistant.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class WordAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(WordAssistantApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 启动时内置默认管理员账号 (仅当 admin 不存在时创建):
     * 用户名 admin, 初始密码 admin123, 登录后可在右上角设置中修改密码。
     * 同时拦截了注册 admin 用户名的通道, 防止被他人抢注。
     */
    @Bean
    public CommandLineRunner seedDefaultAdminRunner(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ADMIN");
                userRepository.save(admin);
                System.out.println(">>> [初始化] 已创建默认管理员账号: admin / admin123 (请登录后在设置中修改密码)");
            }
        };
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
