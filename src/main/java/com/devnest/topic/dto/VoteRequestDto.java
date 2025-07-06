package com.devnest.topic.dto;

import com.devnest.topic.domain.Vote;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoteRequestDto {
    @NotNull(message = "대상 ID가 필요합니다.")
    private Long targetId;
    private Vote.VoteType type;
    private Long userId;
}
