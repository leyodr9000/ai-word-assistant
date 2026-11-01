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
     * 启动时创建管理员账号 admin (仅当 admin 不存在时)。
     * 初始口令取环境变量 ADMIN_INITIAL_PASSWORD；未设置时随机生成一个，只在本次启动日志里打印一次。
     * 登录后可在右上角设置中修改。注册接口拦截了 admin 用户名, 防止被他人抢注。
     */
    @Bean
    public CommandLineRunner seedDefaultAdminRunner(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                String initial = System.getenv("ADMIN_INITIAL_PASSWORD");
                boolean generated = initial == null || initial.isBlank();
                if (generated) initial = randomPassword(16);
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode(initial));
                admin.setRole("ADMIN");
                userRepository.save(admin);
                if (generated) {
                    System.out.println(">>> [初始化] 已创建管理员账号 admin，随机初始口令: " + initial
                            + "（只显示这一次，请登录后在设置中修改）");
                } else {
                    System.out.println(">>> [初始化] 已创建管理员账号 admin，初始口令取自环境变量 ADMIN_INITIAL_PASSWORD");
                }
            }
        };
    }

    private static String randomPassword(int len) {
        final String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        java.security.SecureRandom r = new java.security.SecureRandom();
        StringBuilder b = new StringBuilder(len);
        for (int i = 0; i < len; i++) b.append(chars.charAt(r.nextInt(chars.length())));
        return b.toString();
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
