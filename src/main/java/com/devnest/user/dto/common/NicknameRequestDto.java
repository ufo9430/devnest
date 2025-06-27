package com.devnest.user.dto.common;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NicknameRequestDto {

    @Notblank(message = "닉네임은 필수입니다.")
    //dependency 추가 요망
    private String nickname;
}


