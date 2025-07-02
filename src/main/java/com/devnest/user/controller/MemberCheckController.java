package com.devnest.user.controller;


import com.devnest.auth.domain.CustomUserDetails;
import com.devnest.user.dto.common.NicknameRequestDto;
import com.devnest.user.service.MemberCheckService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")// 단일책임 원칙으로 AuthController와 별개로 작성했습니다
public class MemberCheckController {


    private final MemberCheckService memberCheckService;

    @GetMapping("/check-email")
    public Map<String, Boolean> checkEmailDuplicate(@RequestParam String email) {
        boolean isDuplicate = memberCheckService.isEmailDuplicate(email);
        return Map.of("isDuplicate", isDuplicate);
    }
    //이메일 체크

    @GetMapping("/check-nickname")
    public Map<String, Boolean> checkNicknameDuplicate(@RequestParam String nickname) {
        boolean isDuplicate = memberCheckService.isNicknameDuplicate(nickname);
        return Map.of("isDuplicate", isDuplicate);
    }

    //닉네임 체크

    // 닉네임 변경
    @PatchMapping("/update-nickname")
    public ResponseEntity<?> updateNickname(@RequestBody @Valid NicknameRequestDto dto,
        Authentication authentication) {
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        memberCheckService.updateNickname(userId, dto.getNickname());
        return ResponseEntity.ok("닉네임이 성공적으로 변경되었습니다.");
    }
}