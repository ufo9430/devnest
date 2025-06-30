package com.devnest.user.domain;

public enum NotificationType {
    ANSWER("새로운 답변"), // 내 질문에 새 답변이 달렸을 때
    ACCEPT("답변 채택"), // 내 답변이 채택되었을 때
    RECOMMEND("답변 추천"), // 내 답변이 추천받았을 때
    COMMENT("새로운 댓글"), // 내 게시물/답변에 댓글이 달렸을 때
    MENTION("멘션"), // 누군가 나를 언급했을 때
    FOLLOW("팔로우"), // 누군가 나를 팔로우했을 때
    SYSTEM("시스템"); // 시스템 공지사항

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}