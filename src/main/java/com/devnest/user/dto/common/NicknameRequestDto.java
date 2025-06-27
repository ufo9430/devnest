package com.devnest.user.dto.common;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NicknameRequestDto {

    @NotBlank(message = "닉네임은 필수입니다.")
    //dependency 추가 요망
    private String nickname;
}


