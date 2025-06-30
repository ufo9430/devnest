package com.devnest.user.repository;

import com.devnest.user.domain.Notification;
import com.devnest.user.domain.NotificationType;
import com.devnest.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 사용자별 알림 목록 조회 (최신순)
    Page<Notification> findByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);

    // 사용자별 읽지 않은 알림 목록 조회
    Page<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(User recipient, Pageable pageable);

    // 사용자별 읽지 않은 알림 개수 조회
    long countByRecipientAndIsReadFalse(User recipient);

    // 사용자별 전체 알림 개수 조회
    long countByRecipient(User recipient);

    // 특정 기간 내 알림 조회
    List<Notification> findByRecipientAndCreatedAtAfterOrderByCreatedAtDesc(
            User recipient, LocalDateTime dateTime);

    // 알림 타입별 조회
    Page<Notification> findByRecipientAndTypeOrderByCreatedAtDesc(
            User recipient, NotificationType type, Pageable pageable);

    // 사용자의 모든 알림을 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt " +
            "WHERE n.recipient = :recipient AND n.isRead = false")
    int markAllAsReadByRecipient(@Param("recipient") User recipient, @Param("readAt") LocalDateTime readAt);

    // 특정 알림들을 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt " +
            "WHERE n.notificationId IN :notificationIds AND n.recipient = :recipient")
    int markAsReadByIds(@Param("notificationIds") List<Long> notificationIds,
            @Param("recipient") User recipient,
            @Param("readAt") LocalDateTime readAt);

    // 오래된 알림 삭제 (예: 30일 이상)
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate")
    int deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);

    // 특정 대상에 대한 알림 조회 (예: 특정 질문/답변에 대한 알림)
    List<Notification> findByTargetTypeAndTargetId(String targetType, Long targetId);

    // 최근 N개의 알림 조회
    List<Notification> findTop10ByRecipientOrderByCreatedAtDesc(User recipient);
}