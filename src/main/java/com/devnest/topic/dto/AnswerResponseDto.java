package com.devnest.topic.dto;

import com.devnest.topic.domain.Answer;
import com.devnest.user.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
public class AnswerResponseDto {
    private Long id;
    private Long userId;
    private String content; // HTML
    private boolean isAccepted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int voteCount;
    private String timeAgo;
    private String userNickname;
    private String ProfileImage;
    private String markdownContent; // 마크다운 원본

    public AnswerResponseDto(Answer answer) {
        this.id = answer.getId();
        this.userId = answer.getUserId();
        this.content = answer.getContent();
        this.isAccepted = answer.isAccepted();
        this.createdAt = answer.getCreatedAt();
        this.updatedAt = answer.getUpdatedAt();
        this.timeAgo = timeAgo(answer.getCreatedAt());
    }

    public AnswerResponseDto(Answer answer, User user) {
        this.id = answer.getId();
        this.userId = answer.getUserId();
        this.content = answer.getContent();
        this.isAccepted = answer.isAccepted();
        this.createdAt = answer.getCreatedAt();
        this.updatedAt = answer.getUpdatedAt();
        this.markdownContent = answer.getMarkdownContent();
        this.userNickname = user != null ? user.getNickname() : null;
        this.ProfileImage = user != null ? user.getProfileImage() : null;
    }

    private String timeAgo(LocalDateTime createdAt) {
        if (createdAt == null) return "";

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdAt, now);

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";
        if (hours < 24) return hours + "시간 전";
        if (days < 7) return days + "일 전";
        if (days < 30) return (days / 7) + "주 전";
        if (days < 365) return (days / 30) + "달 전";
        return (days / 365) + "년 전";
    }
}