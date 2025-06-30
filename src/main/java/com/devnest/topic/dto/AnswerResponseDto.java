package com.devnest.topic.dto;

import com.devnest.topic.domain.Answer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnswerResponseDto {
    private Long id;
    private Long userId;
    private String content;
    private boolean isAccepted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AnswerResponseDto(Answer answer) {
        this.id = answer.getId();
        this.userId = answer.getUserId();
        this.content = answer.getContent();
        this.isAccepted = answer.isAccepted();
        this.createdAt = answer.getCreatedAt();
        this.updatedAt = answer.getUpdatedAt();
    }
}