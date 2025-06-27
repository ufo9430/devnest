package com.devnest.user.dto.common;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class EmailRequestDto {

    @NotBlank
    @Email

    private String email;

    //이메일 형식 관련 DTO입니다
    //dependency 추가 요망
}
