package com.devnest.domain.notification.repository;

import com.devnest.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 사용자의 알림 조회 (페이징)
    Page<Notification> findByRecipientUserIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    // 특정 사용자의 읽지 않은 알림 조회 (페이징)
    Page<Notification> findByRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    // 특정 사용자의 최근 알림 조회 (제한된 개수)
    List<Notification> findTop10ByRecipientUserIdOrderByCreatedAtDesc(Long recipientId);

    // 특정 사용자의 읽지 않은 알림 개수
    Long countByRecipientUserIdAndIsReadFalse(Long recipientId);

    // 특정 사용자의 전체 알림 개수
    Long countByRecipientUserId(Long recipientId);

    // 특정 사용자의 모든 알림을 읽음으로 표시
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.recipient.userId = :recipientId AND n.isRead = false")
    int markAllAsReadByRecipientId(@Param("recipientId") Long recipientId, @Param("readAt") LocalDateTime readAt);

    // 특정 알림들을 읽음으로 표시
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.notificationId IN :notificationIds AND n.recipient.userId = :recipientId")
    int markAsReadByIds(@Param("notificationIds") List<Long> notificationIds, @Param("recipientId") Long recipientId,
            @Param("readAt") LocalDateTime readAt);

    // 오래된 알림 삭제 (예: 30일 이전)
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate")
    int deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);

    // 특정 기간 이전의 읽은 알림 삭제
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.isRead = true AND n.createdAt < :cutoffDate")
    int deleteOldReadNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
}