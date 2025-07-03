package com.devnest.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProfileResponseDto {
  private String email;
  private String nickname;
  private String profileImage;
}
