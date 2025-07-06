package com.devnest.topic.dto;

import com.devnest.topic.domain.Topic.TopicStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopicRequestDto {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotEmpty(message = "태그를 1개 이상 입력해주세요.")
    private List<String> tags;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    private TopicStatus status = TopicStatus.WAITING;

    private String markdownContent; // 마크다운 원본 (필수)
}