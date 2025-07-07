package com.devnest.topic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerAcceptRequestDto {
    @NotNull(message = "답변 ID가 필요합니다.")
    private Long answerId;

    @NotNull(message = "질문 ID가 필요합니다.")
    private Long topicId;

    @NotNull(message = "사용자 ID가 필요합니다.")
    private Long userId;
}