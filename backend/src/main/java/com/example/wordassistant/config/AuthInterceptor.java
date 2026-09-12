package com.example.wordassistant.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 基于 HttpSession 的轻量鉴权拦截器:
 * - /api/progress/** /api/chat/** /api/books/** 需要登录
 * - /api/admin/** 需要登录且角色为 ADMIN
 * 登录/注册成功后由 AuthController 写入 session(userId / role)。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 CORS 预检
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        Object userId = request.getSession().getAttribute("userId");
        if (userId == null) {
            reject(response, 401, "未登录或会话已过期,请重新登录");
            return false;
        }

        String uri = request.getRequestURI();
        if (uri.startsWith("/api/admin/")
                && !"ADMIN".equals(request.getSession().getAttribute("role"))) {
            reject(response, 403, "需要管理员权限");
            return false;
        }
        return true;
    }

    private void reject(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
