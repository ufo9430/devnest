package com.devnest.user.controller;

import com.devnest.user.dto.request.EmailCodeVerifyRequestDto;
import com.devnest.user.dto.request.EmailVerificationRequestDto;
import com.devnest.user.dto.request.RegisterRequestDto;
import com.devnest.user.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send-verification")
    public String sendVerificationEmail(@RequestBody @Valid EmailVerificationRequestDto dto) {
        String email = dto.getEmail();
        emailService.sendVerificationCode(email);
        return "✅ 인증코드 전송 완료: " + email;
    }
    // 인증코드 검증
    @PostMapping("/verify-code")
    public String verifyEmailCode(@RequestBody @Valid EmailCodeVerifyRequestDto dto) {
        boolean result = emailService.verifyCode(dto.getEmail(), dto.getCode());
        return result
                ? "✅ 인증 성공: " + dto.getEmail()
                : "❌ 인증 실패: 코드가 일치하지 않음";
    }


}
