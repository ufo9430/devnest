package com.devnest.topic.dto;

import com.devnest.topic.domain.Topic;
import com.devnest.topic.domain.Topic.TopicStatus;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopicResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private String content; // HTML
    private int viewCount;
    private TopicStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AnswerResponseDto> answers;
    private List<String> tags; // 태그 이름만 필요
    private String userNickname;
    private String timeAgo;
    private String ProfileImage;
    private int answerCount;
    private int voteCount;
    private String markdownContent; // 마크다운 원본
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();
    @Builder.Default
    private List<String> fileUrls = new ArrayList<>();


    public TopicResponseDto(Topic topic) {
        this.id = topic.getId();
        this.userId = topic.getUser().getUserId();
        this.title = topic.getTitle();
        this.content = topic.getContent();
        this.viewCount = topic.getViewCount();
        this.status = topic.getStatus();
        this.createdAt = topic.getCreatedAt();
        this.updatedAt = topic.getUpdatedAt();
        this.answers = topic.getAnswers().stream()
                .map(AnswerResponseDto::new)
                .collect(Collectors.toList());
        this.tags = topic.getTags().stream()
                .map(tag -> tag.getName())
                .collect(Collectors.toList());
        // this.userNickname = userNickname;
        this.timeAgo = timeAgo(topic.getCreatedAt());
        // this.ProfileImage = ProfileImage;
        this.answerCount = topic.getAnswers() != null ? topic.getAnswers().size() : 0;
        this.markdownContent = topic.getMarkdownContent();
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