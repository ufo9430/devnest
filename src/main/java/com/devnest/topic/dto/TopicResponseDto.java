package com.devnest.topic.dto;

import com.devnest.topic.domain.Topic;
import com.devnest.topic.domain.Topic.TopicStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class TopicResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private int viewCount;
    private TopicStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AnswerResponseDto> answers;

    public TopicResponseDto(Topic topic) {
        this.id = topic.getId();
        this.userId = topic.getUserId();
        this.title = topic.getTitle();
        this.content = topic.getContent();
        this.viewCount = topic.getViewCount();
        this.status = topic.getStatus();
        this.createdAt = topic.getCreatedAt();
        this.updatedAt = topic.getUpdatedAt();
        this.answers = topic.getAnswers().stream()
                .map(AnswerResponseDto::new)
                .collect(Collectors.toList());
    }
}