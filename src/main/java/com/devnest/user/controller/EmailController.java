package com.devnest.user.controller;

import com.devnest.user.dto.request.EmailCodeVerifyRequestDto;
import com.devnest.user.dto.request.EmailVerificationRequestDto;
import com.devnest.user.dto.request.RegisterRequestDto;
import com.devnest.user.service.EmailService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    // 둘 다 문자열이 아닌 JSON 응답 방식으로 수정

    @PostMapping("/send-verification")
    public ResponseEntity<Map<String, Object>> sendVerificationEmail(@RequestBody @Valid EmailVerificationRequestDto dto) {
        String email = dto.getEmail();
        emailService.sendVerificationCode(email);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "인증코드 전송 완료: " + email);

        return ResponseEntity.ok(response);
    }

    // 인증코드 검증
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifyEmailCode(@RequestBody @Valid EmailCodeVerifyRequestDto dto) {
        boolean result = emailService.verifyCode(dto.getEmail(), dto.getCode());
        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("message", result ? "인증 성공" : "인증 실패: 코드 불일치");
        return result
            ? ResponseEntity.ok(response)
            : ResponseEntity.badRequest().body(response);
    }


}
