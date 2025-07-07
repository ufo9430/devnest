package com.devnest.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPageController {

    @GetMapping("/member-manage")
    public String memberManage(Model model) {
        log.info("🔧 회원 관리 페이지 접근");
        model.addAttribute("title", "회원 관리");
        return "admin_member_manage";
    }

    @GetMapping("/report-manage")
    public String reportManage(Model model) {
        log.info("🔧 신고 관리 페이지 접근");
        model.addAttribute("title", "신고 관리");
        return "report_management";
    }

    @GetMapping("")
    public String adminMain(Model model) {
        log.info("🔧 관리자 메인 페이지 접근");
        model.addAttribute("title", "관리자 대시보드");
        return "admin_main";
    }
}