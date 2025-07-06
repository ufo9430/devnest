package com.devnest.topic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequestDto {
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    private Long userId;

    @NotNull(message = "질문 ID가 필요합니다.")
    private Long topicId;

    private String markdownContent; // 마크다운 원본 (필수)
}