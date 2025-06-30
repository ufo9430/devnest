package com.devnest.user.domain;

public enum ReportStatus {
    PENDING("대기"), // 처리 대기
    APPROVED("승인"), // 승인됨 (신고 유효)
    REJECTED("반려"); // 반려됨 (신고 무효)

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}