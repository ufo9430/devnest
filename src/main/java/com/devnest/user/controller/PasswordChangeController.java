package com.devnest.user.controller;

import com.devnest.user.dto.request.PasswordChangeRequestDto;
import com.devnest.user.service.PasswordChangeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class PasswordChangeController {

    private final PasswordChangeService passwordChangeService;

    // 현재 비밀번호 확인
    @PostMapping("/verify-current-password")
    public ResponseEntity<String> verifyCurrentPassword(@RequestBody PasswordChangeRequestDto requestDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("LOGIN_USER");
        if (userId == null) {
            return ResponseEntity.status(403).body("로그인이 필요합니다.");
        }

        boolean valid = passwordChangeService.verifyCurrentPassword(userId, requestDto.getCurrentPassword());

        if (valid) {
            return ResponseEntity.ok("비밀번호가 일치합니다.");
        } else {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }
    }

    // 새 비밀번호 설정
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid PasswordChangeRequestDto requestDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("LOGIN_USER");
        if (userId == null) {
            return ResponseEntity.status(403).body("로그인이 필요합니다.");
        }

        if (!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        passwordChangeService.changePassword(userId, requestDto.getCurrentPassword(), requestDto.getNewPassword());

        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }
}