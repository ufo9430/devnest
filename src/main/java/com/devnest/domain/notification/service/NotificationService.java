package com.devnest.domain.notification.service;

import com.devnest.domain.notification.entity.Notification;
import com.devnest.domain.notification.entity.NotificationType;
import com.devnest.user.domain.User;
import com.devnest.domain.notification.dto.response.NotificationListResponseDto;
import com.devnest.domain.notification.dto.response.NotificationStatsResponseDto;
import com.devnest.domain.notification.repository.NotificationRepository;
import com.devnest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * 사용자의 알림 목록 조회
     */
    public Page<NotificationListResponseDto> getNotifications(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Page<Notification> notifications = notificationRepository
                .findByRecipientOrderByCreatedAtDesc(user, pageable);

        return notifications.map(NotificationListResponseDto::from);
    }

    /**
     * 읽지 않은 알림만 조회
     */
    public Page<NotificationListResponseDto> getUnreadNotifications(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Page<Notification> notifications = notificationRepository
                .findByRecipientAndIsReadFalseOrderByCreatedAtDesc(user, pageable);

        return notifications.map(NotificationListResponseDto::from);
    }

    /**
     * 알림 통계 조회
     */
    public NotificationStatsResponseDto getNotificationStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        long unreadCount = notificationRepository.countByRecipientAndIsReadFalse(user);
        long totalCount = notificationRepository.countByRecipient(user);

        return NotificationStatsResponseDto.of(unreadCount, totalCount);
    }

    /**
     * 특정 알림 읽음 처리
     */
    @Transactional
    public Boolean markAsRead(Long notificationId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));

        // 알림 소유자 확인
        if (!notification.getRecipient().getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 알림에 대한 권한이 없습니다.");
        }

        if (!notification.getIsRead()) {
            notification.markAsRead();
            notificationRepository.save(notification);
        }

        return true;
    }

    /**
     * 모든 알림 읽음 처리
     */
    @Transactional
    public Integer markAllAsRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return notificationRepository.markAllAsReadByRecipient(user, LocalDateTime.now());
    }

    /**
     * 선택한 알림들 읽음 처리
     */
    @Transactional
    public Integer markAsReadByIds(List<Long> notificationIds, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return notificationRepository.markAsReadByIds(notificationIds, user, LocalDateTime.now());
    }

    /**
     * 알림 생성 - 새 답변 알림
     */
    @Transactional
    public void createAnswerNotification(Long recipientId, Long senderId, String questionTitle, Long questionId) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("수신자를 찾을 수 없습니다."));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("발신자를 찾을 수 없습니다."));

        // 자기 자신에게는 알림을 보내지 않음
        if (recipientId.equals(senderId)) {
            return;
        }

        Notification notification = Notification.createAnswerNotification(
                recipient, sender, questionTitle, questionId);

        notificationRepository.save(notification);
    }

    /**
     * 알림 생성 - 답변 채택 알림
     */
    @Transactional
    public void createAcceptNotification(Long recipientId, String questionTitle, Long answerId) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("수신자를 찾을 수 없습니다."));

        Notification notification = Notification.createAcceptNotification(
                recipient, questionTitle, answerId);

        notificationRepository.save(notification);
    }

    /**
     * 알림 생성 - 답변 추천 알림
     */
    @Transactional
    public void createRecommendNotification(Long recipientId, Long senderId, String questionTitle, Long answerId) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("수신자를 찾을 수 없습니다."));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("발신자를 찾을 수 없습니다."));

        // 자기 자신에게는 알림을 보내지 않음
        if (recipientId.equals(senderId)) {
            return;
        }

        Notification notification = Notification.createRecommendNotification(
                recipient, sender, questionTitle, answerId);

        notificationRepository.save(notification);
    }

    /**
     * 시스템 알림 생성
     */
    @Transactional
    public void createSystemNotification(Long recipientId, String message) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("수신자를 찾을 수 없습니다."));

        Notification notification = Notification.createSystemNotification(recipient, message);
        notificationRepository.save(notification);
    }

    /**
     * 모든 사용자에게 시스템 알림 발송
     */
    @Transactional
    public Integer broadcastSystemNotification(String message) {
        List<User> activeUsers = userRepository.findByIsActiveTrue();
        int count = 0;

        for (User user : activeUsers) {
            try {
                createSystemNotification(user.getUserId(), message);
                count++;
            } catch (Exception e) {
                // 개별 실패는 무시하고 계속 진행
            }
        }

        return count;
    }

    /**
     * 오래된 알림 정리 (30일 이상)
     */
    @Transactional
    public Integer cleanupOldNotifications() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        return notificationRepository.deleteOldNotifications(cutoffDate);
    }

    /**
     * 최근 알림 목록 조회 (헤더 드롭다운용)
     */
    public List<NotificationListResponseDto> getRecentNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Notification> notifications = notificationRepository
                .findTop10ByRecipientOrderByCreatedAtDesc(user);

        return notifications.stream()
                .map(NotificationListResponseDto::from)
                .toList();
    }
}