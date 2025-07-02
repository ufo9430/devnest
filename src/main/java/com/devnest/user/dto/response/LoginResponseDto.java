package com.devnest.user.dto.response;

import com.devnest.user.domain.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {
  private Long userId;
  private String email;
  private String nickname;
  private Role role;
}
