package com.devnest.global.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileVo {
    private Long user_id;
    private String profileImage;
}
