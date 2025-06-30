package com.devnest.user.controller;


import com.devnest.user.service.MemberCheckService;
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
    public String checkEmailDuplicate(@RequestParam String email) {
        boolean isDuplicate = memberCheckService.isEmailDuplicate(email);
        return isDuplicate ? "❌ 이미 사용 중인 이메일입니다." : "✅ 사용 가능한 이메일입니다.";
    }
    //이메일 체크

    @GetMapping("/check-nickname")
    public String checkNicknameDuplicate(@RequestParam String nickname) {
        boolean isDuplicate = memberCheckService.isNicknameDuplicate(nickname);
        return isDuplicate ? "❌ 이미 사용 중인 닉네임입니다." : "✅ 사용 가능한 닉네임입니다.";
    }

    //닉네임 체크
}
