package com.devnest.user.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class TempController {

  @GetMapping()
  public String showHome(HttpSession session, Model model) {
    Object user = session.getAttribute("LOGIN_USER"); // 로그인 시 세션에 저장한 이름으로 가져오기
    if (user != null) {
      System.out.println("Session User: " + user);
    } else {
      System.out.println("로그인된 사용자 없음");
    }
    model.addAttribute("sessionUser", user);
    return "index";
  }

}
