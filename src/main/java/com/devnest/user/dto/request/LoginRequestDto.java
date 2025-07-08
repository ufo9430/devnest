package com.devnest.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

  @NotBlank(message = "이메일은 필수입니다.")
  @Email(message = "유효한 이메일 형식이어야 합니다.")
  private String email;

  @NotBlank(message = "비밀번호는 필수입니다.")
  private String password;
}
