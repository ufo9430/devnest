package com.devnest.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailCodeVerifyRequestDto {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "인증 코드는 필수입니다.")
    private String code;

    //코드 인증용 Dto(⚠️분리해서 사용해야 합니다! ⚠️)
}
