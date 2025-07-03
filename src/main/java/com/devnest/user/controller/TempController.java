package com.devnest.user.controller;

import com.devnest.auth.domain.CustomUserDetails;
import com.devnest.user.service.MemberLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class TempController {

  private final MemberLoginService memberLoginService;

  @GetMapping()
  public String showHome(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.isAuthenticated()
        && !(authentication.getPrincipal() instanceof String)) { // anonymousUser 예외 처리

      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Long userId = userDetails.getUserId();
      String email = userDetails.getEmail();
      System.out.println("userId = " + userId + ", email = " + email);
      model.addAttribute("sessionUser", userDetails);

      if (userDetails.getAuthorities().stream()
          .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
        return "redirect:/admin";
      }
    } else {
      System.out.println("로그인된 사용자 없음");
      model.addAttribute("sessionUser", null);
    }

    return "index";
  }

}
