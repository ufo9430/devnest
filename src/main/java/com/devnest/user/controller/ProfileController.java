package com.devnest.user.controller;

import com.devnest.auth.domain.CustomUserDetails;
import com.devnest.user.domain.User;
import com.devnest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ProfileController {

  private final UserRepository userRepository;

  // Controller 예시
  @GetMapping("/member/profile")
  public String getProfile(Model model, Authentication authentication) {
    // 인증 안 된 경우 로그인 페이지로
    if (authentication == null || !authentication.isAuthenticated()) {
      return "redirect:/member/login";
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    User user = userRepository.findById(userDetails.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

    model.addAttribute("profile", user);
    return "profile";
  }
}
