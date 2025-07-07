package com.devnest.global.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileVo {
    private Long user_id;
    private String profileImage;
    private String email;       // 이메일 추가
    private String nickname;    // 닉네임 추가
}
