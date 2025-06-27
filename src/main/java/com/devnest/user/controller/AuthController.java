package com.devnest.user.controller;

import com.devnest.user.dto.common.ApiResponse;
import com.devnest.user.dto.common.EmailRequestDto;
import com.devnest.user.dto.request.RegisterRequestDto;
import com.devnest.user.dto.response.RegisterResponseDto;
import com.devnest.user.service.MemberRegisterService;
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

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<RegisterResponseDto>> register(
      @RequestBody @Valid RegisterRequestDto requestDto
  ) {
    RegisterResponseDto response = memberRegisterService.register(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("회원가입 성공", response));
  }
}
