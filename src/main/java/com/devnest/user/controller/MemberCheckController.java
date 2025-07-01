package com.devnest.user.controller;


import com.devnest.user.service.MemberCheckService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
}
