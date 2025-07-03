package com.devnest.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SimpleTestController {

    @GetMapping("/test")
    public String testPage(Model model) {
        model.addAttribute("message", "🎉 홈페이지가 정상 작동합니다!");
        model.addAttribute("time", java.time.LocalDateTime.now());
        return "test"; // test.html 템플릿을 찾습니다
    }
}