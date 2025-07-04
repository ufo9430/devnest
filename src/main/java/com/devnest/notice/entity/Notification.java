package com.devnest.notice.entity;

import com.devnest.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient; // 알림을 받는 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender; // 알림을 발생시킨 사용자 (선택적)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    @Column(nullable = false, length = 500)
    private String message; // 알림 메시지

    @Column(name = "title", length = 200)
    private String title; // 관련 게시물/답변 제목

    @Column(name = "target_type", length = 50)
    private String targetType; // 관련 엔티티 타입 (POST, ANSWER, COMMENT 등)

    @Column(name = "target_id")
    private Long targetId; // 관련 엔티티 ID

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false; // 읽음 여부

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt; // 읽은 시간

    // 고도화 기능을 위한 추가 필드들

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    @Builder.Default
    private NotificationPriority priority = NotificationPriority.NORMAL; // 알림 우선순위

    @Column(name = "action_url", length = 500)
    private String actionUrl; // 클릭 시 이동할 URL

    @Column(name = "deletable", nullable = false)
    @Builder.Default
    private Boolean deletable = true; // 사용자가 삭제 가능한지 여부

    @Column(name = "auto_delete_at")
    private LocalDateTime autoDeleteAt; // 자동 삭제 시간

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false; // 삭제 여부 (soft delete)

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 삭제된 시간

    @Column(name = "additional_data", length = 1000)
    private String additionalData; // 추가 데이터 (JSON 형태)

    // 편의 메서드
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    public void markAsUnread() {
        this.isRead = false;
        this.readAt = null;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

    public boolean isExpired() {
        return autoDeleteAt != null && LocalDateTime.now().isAfter(autoDeleteAt);
    }

    // 알림 생성 편의 메서드들
    public static Notification createAnswerNotification(User recipient, User sender, String title, Long questionId) {
        return Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(NotificationType.ANSWER)
                .message(sender.getNickname() + "님이 새로운 답변을 달았습니다.")
                .title(title)
                .targetType("QUESTION")
                .targetId(questionId)
                .priority(NotificationPriority.NORMAL)
                .actionUrl("/questions/" + questionId)
                .build();
    }

    public static Notification createAcceptNotification(User recipient, String title, Long answerId) {
        return Notification.builder()
                .recipient(recipient)
                .type(NotificationType.ACCEPT)
                .message("회원님의 답변이 채택되었습니다.")
                .title(title)
                .targetType("ANSWER")
                .targetId(answerId)
                .priority(NotificationPriority.HIGH)
                .actionUrl("/answers/" + answerId)
                .build();
    }

    public static Notification createRecommendNotification(User recipient, User sender, String title, Long answerId) {
        return Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(NotificationType.RECOMMEND)
                .message(sender.getNickname() + "님이 회원님의 답변을 추천했습니다.")
                .title(title)
                .targetType("ANSWER")
                .targetId(answerId)
                .priority(NotificationPriority.NORMAL)
                .actionUrl("/answers/" + answerId)
                .build();
    }

    public static Notification createSystemNotification(User recipient, String message) {
        return Notification.builder()
                .recipient(recipient)
                .type(NotificationType.SYSTEM)
                .message(message)
                .targetType("SYSTEM")
                .priority(NotificationPriority.HIGH)
                .deletable(false)
                .build();
    }

    public static Notification createReportNotification(User recipient, String title, Long reportId) {
        return Notification.builder()
                .recipient(recipient)
                .type(NotificationType.SYSTEM)
                .message("신고가 접수되었습니다: " + title)
                .title(title)
                .targetType("REPORT")
                .targetId(reportId)
                .priority(NotificationPriority.HIGH)
                .actionUrl("/admin/reports/" + reportId)
                .deletable(false)
                .build();
    }

    public static Notification createSuspendNotification(User recipient, String reason, LocalDateTime until) {
        return Notification.builder()
                .recipient(recipient)
                .type(NotificationType.SYSTEM)
                .message("계정이 정지되었습니다. 사유: " + reason + " (해제일: " + until + ")")
                .targetType("SUSPEND")
                .priority(NotificationPriority.URGENT)
                .deletable(false)
                .build();
    }
}