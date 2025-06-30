package com.devnest.topic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerRequestDto {
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @NotNull(message = "사용자 ID가 필요합니다.")
    private Long userId;

    @NotNull(message = "질문 ID가 필요합니다.")
    private Long topicId;
}