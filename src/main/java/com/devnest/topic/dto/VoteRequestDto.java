package com.devnest.topic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoteRequestDto {
    @NotNull(message = "대상 ID가 필요합니다.")
    private Long targetId;

    @NotNull(message = "사용자 ID가 필요합니다.")
    private Long userId;
}
