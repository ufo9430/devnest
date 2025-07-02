package com.devnest.user.controller;


import com.devnest.user.dto.response.EmailLookUpResponseDto;
import com.devnest.user.service.MemberLookupService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberLookupController {

    private final MemberLookupService memberLookupService;

    @GetMapping("/find-by-email")
    public EmailLookUpResponseDto getUserInfoByEmail(@RequestParam String email) {
        return memberLookupService.getUserInfoByEmail(email);
    }

}
