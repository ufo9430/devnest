package com.devnest.user.dto.common;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NicknameRequestDto {

  @NotBlank(message = "닉네임은 필수입니다.")
  @Size(max = 50)
  private String nickname;
}


