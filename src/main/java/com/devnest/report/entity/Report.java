package com.devnest.report.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId; // 신고자 ID

    @Column(name = "reported_user_id", nullable = false)
    private Long reportedUserId; // 피신고자 ID

    @Column(name = "target_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportTargetType targetType; // 신고 대상 유형 (USER, TOPIC, ANSWER)

    @Column(name = "target_id")
    private Long targetId; // 신고 대상 ID (게시글 ID, 댓글 ID 등)

    @Column(name = "report_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportType reportType; // 신고 유형

    @Column(name = "title", nullable = false, length = 200)
    private String title; // 신고 제목

    @Column(name = "content", nullable = false, length = 1000)
    private String content; // 신고 내용

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.PENDING; // 처리 상태

    @Column(name = "admin_response", length = 1000)
    private String adminResponse; // 관리자 응답

    @Column(name = "processed_by")
    private Long processedBy; // 처리한 관리자 ID

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt; // 처리 완료 시간

    // 신고 처리 메서드
    public void approve(Long adminId, String response) {
        this.status = ReportStatus.APPROVED;
        this.processedBy = adminId;
        this.adminResponse = response;
        this.processedAt = LocalDateTime.now();
    }

    public void reject(Long adminId, String response) {
        this.status = ReportStatus.REJECTED;
        this.processedBy = adminId;
        this.adminResponse = response;
        this.processedAt = LocalDateTime.now();
    }

    public void dismiss(Long adminId, String response) {
        this.status = ReportStatus.DISMISSED;
        this.processedBy = adminId;
        this.adminResponse = response;
        this.processedAt = LocalDateTime.now();
    }
}