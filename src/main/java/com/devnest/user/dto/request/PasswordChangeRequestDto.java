package com.devnest.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequestDto {

    @NotBlank(message = "현재 비밀번호를 입력하세요.")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호를 입력하세요.")
    @Pattern(
            regexp = "^[A-Za-z\\d~!@#$%^&*()_+\\-=\\[\\]{},.<>?/]{8,}$",
            message = "비밀번호는 8자 이상이어야 합니다."
    )
    private String newPassword;

    @NotBlank(message = "새 비밀번호 확인을 입력하세요.")
    private String confirmPassword;
}
