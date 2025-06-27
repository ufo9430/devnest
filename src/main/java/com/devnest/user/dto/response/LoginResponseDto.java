package com.devnest.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class LoginResponseDto {
  private Long userId;
  private String email;
  private String nickname;
}
