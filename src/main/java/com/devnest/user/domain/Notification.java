package com.devnest.user.domain;

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

    // 편의 메서드
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    // 알림 생성 편의 메서드들
    public static Notification createAnswerNotification(User recipient, User sender, String title, Long questionId) {
        return Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(NotificationType.ANSWER)
                .message(sender.getNickname() + "가 새로운 답변을 달았습니다.")
                .title(title)
                .targetType("QUESTION")
                .targetId(questionId)
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
                .build();
    }

    public static Notification createRecommendNotification(User recipient, User sender, String title, Long answerId) {
        return Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(NotificationType.RECOMMEND)
                .message("회원님의 답변이 추천되었습니다.")
                .title(title)
                .targetType("ANSWER")
                .targetId(answerId)
                .build();
    }

    public static Notification createSystemNotification(User recipient, String message) {
        return Notification.builder()
                .recipient(recipient)
                .type(NotificationType.SYSTEM)
                .message(message)
                .targetType("SYSTEM")
                .build();
    }
}