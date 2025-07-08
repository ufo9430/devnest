package com.devnest.user.dto.common;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordRequestDto {
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    //dependency 추가 요망
}
