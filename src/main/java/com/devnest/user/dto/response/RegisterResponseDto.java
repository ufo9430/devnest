package com.devnest.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponseDto {
    private Long userId;
    private String email;
    private String nickname;
}

//API 응답시에 ApiResponse<RegisterResponseDto> 형태로 감싸서 리턴!