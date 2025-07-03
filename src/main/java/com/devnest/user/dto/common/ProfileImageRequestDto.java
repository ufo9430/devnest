package com.devnest.user.dto.common;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileImageRequestDto {
  @Size(max = 500)
  private String profileImage;
}
