package com.devnest.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EmailLookUpResponseDto {

    private final String nickname;
    private final LocalDateTime createdAt;

    //닉네임, 생성일을 리턴 합니다

}
