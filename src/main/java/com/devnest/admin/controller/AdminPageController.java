package com.devnest.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
// @PreAuthorize("hasRole('ADMIN')") // 임시 주석 처리
public class AdminPageController {

  @GetMapping
  public String adminMainPage(Model model) {
    return "admin_member_manage";
  }

  @GetMapping("/member-manage")
  public String adminMemberManage(Model model) {
    return "admin_member_manage";
  }

  @GetMapping("/report-manage")
  public String adminReportManage(Model model) {
    return "report_management";
  }
}
