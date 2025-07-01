package com.devnest.auth.controller;

import com.devnest.user.dto.request.LoginRequestDto;
import com.devnest.user.dto.request.RegisterRequestDto;
import com.devnest.user.service.EmailService;
import com.devnest.user.service.MemberLoginService;
import com.devnest.user.service.MemberRegisterService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class AuthController {

  private final MemberRegisterService memberRegisterService;
  private final MemberLoginService memberLoginService;
  private final EmailService emailService;

  @GetMapping("/signup")
  public String showSignupForm() {
    return "signup";
  }

  @PostMapping("/signup")
  public String processSignup(@ModelAttribute @Valid RegisterRequestDto requestDto, Model model) {
    memberRegisterService.register(requestDto);
    model.addAttribute("message", "회원가입이 완료되었습니다.");
    return "redirect:/member/login";
  }

  @GetMapping("/login")
  public String showLoginForm() {
    return "login";
  }

  @PostMapping("/verify-user")
  @ResponseBody
  public ResponseEntity<?> verifyUserAndSendCode(@RequestBody @Valid LoginRequestDto dto, HttpSession session) {
    try {
      // 비밀번호 확인 (1차 검증)
      memberLoginService.verifyUser(dto);

      // 이메일 인증코드 발송 (2차 인증)
      emailService.sendVerificationCode(dto.getEmail());

      return ResponseEntity.ok("CODE_SENT");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/login")
  public String processLogin(@ModelAttribute @Valid LoginRequestDto requestDto, HttpSession session,
      Model model) {
    try {
      memberLoginService.login(requestDto, session);
      return "redirect:/";
    } catch (IllegalArgumentException e) {
      model.addAttribute("active_error", e.getMessage());
      return "login"; // 로그인 페이지로 다시 이동
    }

  }

  @PostMapping("/logout")
  public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/";
  }

  @GetMapping("/find-account")
  public String showFindAccountForm() {
    return "find_account";
  }

  @GetMapping("/password-recovery")
  public String showForgotPasswordForm() {
    return "forgot_password";
  }
}
