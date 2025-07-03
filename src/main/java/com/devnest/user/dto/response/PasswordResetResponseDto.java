package com.devnest.user.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordResetResponseDto {

    private final String tempPassword;
}
