package com.devnest.report.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportTargetType {
    USER("사용자"),
    TOPIC("게시글"),
    ANSWER("답변");

    private final String description;
}