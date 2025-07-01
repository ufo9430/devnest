package com.devnest.topic.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// @Enumerated(EnumType.STRING)을 사용하여 target_type을 Enum으로 관리
// @CreationTimestamp로 생성일시 자동 관리
// @NoArgsConstructor(access = AccessLevel.PROTECTED)로 기본 생성자 제한
// 신고 사유 수정을 위한 편의 메서드 생성

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;  // 신고 고유 ID

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;  // 신고자 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 10)
    @NotNull(message = "targetType은 필수입니다.")
    private TargetType targetType;  // 신고 대상 타입

    @Column(name = "target_id", nullable = false)
    private Long targetId;  // 신고 대상 ID

    @Column(nullable = false, length = 500)
    private String reason;  // 신고 사유

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 신고일시

    @Builder
    public Report(Long reporterId, TargetType targetType, Long targetId, String reason) {
        this.reporterId = reporterId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
    }

    // 신고 대상 타입을 나타내는 Enum
    public enum TargetType {
        TOPIC,   // 질문
        ANSWER   // 답변
    }

    // 신고 내용 수정 메서드
    public void updateReason(String reason) {
        this.reason = reason;
    }
}