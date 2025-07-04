package com.devnest.report.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {
    PENDING("처리대기"),
    APPROVED("승인됨"),
    REJECTED("거부됨"),
    DISMISSED("기각됨");

    private final String description;
}