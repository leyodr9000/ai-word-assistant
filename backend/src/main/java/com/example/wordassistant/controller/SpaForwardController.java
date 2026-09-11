package com.example.wordassistant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 绿色版 (单 JAR 内嵌前端) SPA 路由转发。
 * Vue Router 使用 history 模式, 刷新 /home /library 等路径时
 * 需要服务器把非静态资源请求转发回 index.html。
 * 带 "." 的路径 (如 /assets/index-abc123.js) 交给静态资源处理器,
 * /api/** 为两级路径不会命中本控制器。
 */
@Controller
public class SpaForwardController {

    @GetMapping({"/", "/login", "/home", "/library", "/admin", "/{path:[^\\.]*}"})
    public String forwardSpa() {
        return "forward:/index.html";
    }
}
