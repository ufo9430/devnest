package com.devnest.user.dto.common;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationCodeDto {

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;


    @NotBlank(message = "인증 코드는 필수입니다.")
    private String code;
}
