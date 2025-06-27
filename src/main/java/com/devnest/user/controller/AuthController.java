package com.devnest.user.controller;

import com.devnest.user.dto.common.ApiResponse;
import com.devnest.user.dto.request.LoginRequestDto;
import com.devnest.user.dto.request.RegisterRequestDto;
import com.devnest.user.dto.response.LoginResponseDto;
import com.devnest.user.dto.response.RegisterResponseDto;
import com.devnest.user.service.MemberLoginService;
import com.devnest.user.service.MemberRegisterService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class AuthController {

  private final MemberRegisterService memberRegisterService;
  private final MemberLoginService memberLoginService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<RegisterResponseDto>> register(
      @RequestBody @Valid RegisterRequestDto requestDto
  ) {
    RegisterResponseDto responseDto = memberRegisterService.register(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("회원가입 성공", responseDto));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponseDto>> login(
      @RequestBody @Valid LoginRequestDto requestDto,
      HttpSession session
  ) {
    LoginResponseDto responseDto = memberLoginService.login(requestDto, session);
    return ResponseEntity.ok(ApiResponse.success("로그인 성공", responseDto));
  }
}
