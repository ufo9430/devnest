package com.devnest.notice.repository;

import com.devnest.notice.entity.Notification;
import com.devnest.notice.entity.NotificationPriority;
import com.devnest.notice.entity.NotificationType;
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

    // 특정 사용자의 알림 조회 (페이징) - 삭제되지 않은 것만
    @Query("SELECT n FROM Notification n WHERE n.recipient.userId = :recipientId AND n.isDeleted = false ORDER BY n.priority DESC, n.createdAt DESC")
    Page<Notification> findByRecipientUserIdOrderByPriorityAndCreatedAtDesc(@Param("recipientId") Long recipientId,
            Pageable pageable);

    // 특정 사용자의 읽지 않은 알림 조회 (페이징) - 삭제되지 않은 것만
    @Query("SELECT n FROM Notification n WHERE n.recipient.userId = :recipientId AND n.isRead = false AND n.isDeleted = false ORDER BY n.priority DESC, n.createdAt DESC")
    Page<Notification> findByRecipientUserIdAndIsReadFalseOrderByPriorityAndCreatedAtDesc(
            @Param("recipientId") Long recipientId, Pageable pageable);

    // 특정 사용자의 최근 알림 조회 (제한된 개수) - 삭제되지 않은 것만
    @Query("SELECT n FROM Notification n WHERE n.recipient.userId = :recipientId AND n.isDeleted = false ORDER BY n.priority DESC, n.createdAt DESC")
    List<Notification> findTop10ByRecipientUserIdOrderByPriorityAndCreatedAtDesc(@Param("recipientId") Long recipientId,
            Pageable pageable);

    // 특정 사용자의 읽지 않은 알림 개수 - 삭제되지 않은 것만
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient.userId = :recipientId AND n.isRead = false AND n.isDeleted = false")
    Long countByRecipientUserIdAndIsReadFalseAndNotDeleted(@Param("recipientId") Long recipientId);

    // 특정 사용자의 전체 알림 개수 - 삭제되지 않은 것만
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient.userId = :recipientId AND n.isDeleted = false")
    Long countByRecipientUserIdAndNotDeleted(@Param("recipientId") Long recipientId);

    /**
     * 타입별 알림 조회
     */
    @Query("SELECT n FROM Notification n WHERE n.recipient.userId = :recipientId AND n.type = :type AND n.isDeleted = false ORDER BY n.priority DESC, n.createdAt DESC")
    Page<Notification> findByRecipientUserIdAndType(@Param("recipientId") Long recipientId,
            @Param("type") NotificationType type,
            Pageable pageable);

    /**
     * 우선순위별 알림 조회
     */
    @Query("SELECT n FROM Notification n WHERE n.recipient.userId = :recipientId AND n.priority = :priority AND n.isDeleted = false ORDER BY n.createdAt DESC")
    Page<Notification> findByRecipientUserIdAndPriority(@Param("recipientId") Long recipientId,
            @Param("priority") NotificationPriority priority,
            Pageable pageable);

    /**
     * 알림 검색 (제목, 메시지 내용 검색)
     */
    @Query("SELECT n FROM Notification n WHERE n.recipient.userId = :recipientId AND " +
            "(n.message LIKE %:keyword% OR n.title LIKE %:keyword%) AND n.isDeleted = false " +
            "ORDER BY n.priority DESC, n.createdAt DESC")
    Page<Notification> searchByKeyword(@Param("recipientId") Long recipientId,
            @Param("keyword") String keyword,
            Pageable pageable);

    /**
     * 복합 필터 검색
     */
    @Query("SELECT n FROM Notification n WHERE n.recipient.userId = :recipientId AND " +
            "(:type IS NULL OR n.type = :type) AND " +
            "(:priority IS NULL OR n.priority = :priority) AND " +
            "(:isRead IS NULL OR n.isRead = :isRead) AND " +
            "(:keyword IS NULL OR n.message LIKE %:keyword% OR n.title LIKE %:keyword%) AND " +
            "n.isDeleted = false " +
            "ORDER BY n.priority DESC, n.createdAt DESC")
    Page<Notification> findWithFilters(@Param("recipientId") Long recipientId,
            @Param("type") NotificationType type,
            @Param("priority") NotificationPriority priority,
            @Param("isRead") Boolean isRead,
            @Param("keyword") String keyword,
            Pageable pageable);

    /**
     * 긴급 알림 조회
     */
    @Query("SELECT n FROM Notification n WHERE n.recipient.userId = :recipientId AND " +
            "n.priority = 'URGENT' AND n.isRead = false AND n.isDeleted = false " +
            "ORDER BY n.createdAt DESC")
    List<Notification> findUrgentUnreadNotifications(@Param("recipientId") Long recipientId);

    /**
     * 삭제 가능한 알림들만 조회
     */
    @Query("SELECT n FROM Notification n WHERE n.recipient.userId = :recipientId AND " +
            "n.deletable = true AND n.isDeleted = false " +
            "ORDER BY n.priority DESC, n.createdAt DESC")
    Page<Notification> findDeletableNotifications(@Param("recipientId") Long recipientId, Pageable pageable);

    /**
     * 특정 알림들을 소프트 삭제
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isDeleted = true, n.deletedAt = :deletedAt " +
            "WHERE n.notificationId IN :notificationIds AND n.recipient.userId = :recipientId AND n.deletable = true")
    int softDeleteByIds(@Param("notificationIds") List<Long> notificationIds,
            @Param("recipientId") Long recipientId,
            @Param("deletedAt") LocalDateTime deletedAt);

    /**
     * 특정 사용자의 모든 삭제 가능한 알림을 소프트 삭제
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isDeleted = true, n.deletedAt = :deletedAt " +
            "WHERE n.recipient.userId = :recipientId AND n.deletable = true AND n.isDeleted = false")
    int softDeleteAllByRecipientId(@Param("recipientId") Long recipientId, @Param("deletedAt") LocalDateTime deletedAt);

    /**
     * 읽은 알림들만 소프트 삭제
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isDeleted = true, n.deletedAt = :deletedAt " +
            "WHERE n.recipient.userId = :recipientId AND n.isRead = true AND n.deletable = true AND n.isDeleted = false")
    int softDeleteReadNotifications(@Param("recipientId") Long recipientId,
            @Param("deletedAt") LocalDateTime deletedAt);

    /**
     * 자동 삭제 시간이 지난 알림들 조회
     */
    @Query("SELECT n FROM Notification n WHERE n.autoDeleteAt IS NOT NULL AND n.autoDeleteAt <= :currentTime AND n.isDeleted = false")
    List<Notification> findExpiredNotifications(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 소프트 삭제된 알림들을 완전 삭제
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.isDeleted = true AND n.deletedAt < :cutoffDate")
    int deleteOldSoftDeletedNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
}