package com.devnest.board.dto;

import com.devnest.topic.domain.Tag;
import com.devnest.topic.domain.Topic;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class TopicResponseDTO {
    private Long topicId;
    private String nickname;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer answerCount;
    private Topic.TopicStatus status;
    private List<String> tags;
    private LocalDateTime createdAt;

    public TopicResponseDTO(Topic topic) {
        this.topicId = topic.getId();
        this.title = topic.getTitle();
        this.content = topic.getContent();
        this.nickname = topic.getUser().getNickname();
        this.viewCount = topic.getViewCount();
        this.answerCount = topic.getAnswers().size();
        this.status = topic.getStatus();
        this.createdAt = topic.getCreatedAt();
        this.tags = new ArrayList<>();

        for (Tag tag : topic.getTags()) {
            tags.add(tag.getName());
        }
    }
}
