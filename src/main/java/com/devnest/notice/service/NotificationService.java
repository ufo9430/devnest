package com.devnest.notice.service;

import com.devnest.notice.entity.Notification;
import com.devnest.notice.entity.NotificationType;
import com.devnest.user.domain.User;
import com.devnest.notice.dto.response.NotificationListResponseDto;
import com.devnest.notice.dto.response.NotificationStatsResponseDto;
import com.devnest.notice.repository.NotificationRepository;
import com.devnest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import org.springframework.data.domain.PageImpl;
import java.util.stream.Collectors;

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
        // 사용자가 존재하지 않는 경우 더미 데이터 반환
        if (!userRepository.existsById(userId)) {
            return createDummyNotifications(pageable);
        }

        Page<Notification> notifications = notificationRepository
                .findByRecipientUserIdOrderByCreatedAtDesc(userId, pageable);

        return notifications.map(NotificationListResponseDto::from);
    }

    /**
     * 읽지 않은 알림만 조회
     */
    public Page<NotificationListResponseDto> getUnreadNotifications(Long userId, Pageable pageable) {
        // 사용자가 존재하지 않는 경우 더미 데이터 반환
        if (!userRepository.existsById(userId)) {
            return createDummyUnreadNotifications(pageable);
        }

        Page<Notification> notifications = notificationRepository
                .findByRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable);

        return notifications.map(NotificationListResponseDto::from);
    }

    /**
     * 알림 통계 조회
     */
    public NotificationStatsResponseDto getNotificationStats(Long userId) {
        // 사용자가 존재하지 않는 경우 더미 데이터 반환
        if (!userRepository.existsById(userId)) {
            return NotificationStatsResponseDto.of(12, 45); // 더미 통계 데이터
        }

        long unreadCount = notificationRepository.countByRecipientUserIdAndIsReadFalse(userId);
        long totalCount = notificationRepository.countByRecipientUserId(userId);

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

        return notificationRepository.markAllAsReadByRecipientId(userId, LocalDateTime.now());
    }

    /**
     * 선택한 알림들 읽음 처리
     */
    @Transactional
    public Integer markAsReadByIds(List<Long> notificationIds, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return notificationRepository.markAsReadByIds(notificationIds, userId, LocalDateTime.now());
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
        List<User> activeUsers = userRepository.findAll(); // 일단 모든 사용자로 변경
        int count = 0;

        for (User user : activeUsers) {
            try {
                createSystemNotification(user.getUserId(), message);
                count++;
            } catch (Exception e) {
                // 로그 출력하고 계속 진행
                System.err.println("Failed to send notification to user " + user.getUserId() + ": " + e.getMessage());
            }
        }

        return count;
    }

    /**
     * 오래된 알림 정리 (30일 이전)
     */
    @Transactional
    public Integer cleanupOldNotifications() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        return notificationRepository.deleteOldReadNotifications(cutoffDate);
    }

    /**
     * 최근 알림 목록 조회 (헤더 드롭다운용)
     */
    public List<NotificationListResponseDto> getRecentNotifications(Long userId) {
        // 사용자가 존재하지 않는 경우 더미 데이터 반환
        if (!userRepository.existsById(userId)) {
            return createDummyRecentNotifications();
        }

        List<Notification> notifications = notificationRepository
                .findTop10ByRecipientUserIdOrderByCreatedAtDesc(userId);

        return notifications.stream()
                .map(NotificationListResponseDto::from)
                .toList();
    }

    /**
     * 고도화된 알림 조회 (우선순위와 시간순 정렬)
     */
    public Page<NotificationListResponseDto> getNotificationsAdvanced(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            return createDummyNotifications(pageable);
        }

        Page<Notification> notifications = notificationRepository
                .findByRecipientUserIdOrderByPriorityAndCreatedAtDesc(userId, pageable);

        return notifications.map(NotificationListResponseDto::from);
    }

    /**
     * 고도화된 읽지 않은 알림 조회 (우선순위와 시간순 정렬)
     */
    public Page<NotificationListResponseDto> getUnreadNotificationsAdvanced(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            return createDummyUnreadNotifications(pageable);
        }

        Page<Notification> notifications = notificationRepository
                .findByRecipientUserIdAndIsReadFalseOrderByPriorityAndCreatedAtDesc(userId, pageable);

        return notifications.map(NotificationListResponseDto::from);
    }

    /**
     * 타입별 알림 조회
     */
    @Transactional(readOnly = true)
    public Page<NotificationListResponseDto> getNotificationsByType(Long userId,
            com.devnest.notice.entity.NotificationType type, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Page<Notification> notifications = notificationRepository
                .findByRecipientUserIdAndType(userId, type, pageable);

        return notifications.map(NotificationListResponseDto::from);
    }

    /**
     * 우선순위별 알림 조회
     */
    @Transactional(readOnly = true)
    public Page<NotificationListResponseDto> getNotificationsByPriority(Long userId,
            com.devnest.notice.entity.NotificationPriority priority, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Page<Notification> notifications = notificationRepository
                .findByRecipientUserIdAndPriority(userId, priority, pageable);

        return notifications.map(NotificationListResponseDto::from);
    }

    /**
     * 알림 검색 (키워드로 제목, 메시지 검색)
     */
    @Transactional(readOnly = true)
    public Page<NotificationListResponseDto> searchNotifications(Long userId, String keyword, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Page<Notification> notifications = notificationRepository
                .searchByKeyword(userId, keyword, pageable);

        return notifications.map(NotificationListResponseDto::from);
    }

    /**
     * 복합 필터를 사용한 알림 조회
     */
    @Transactional(readOnly = true)
    public Page<NotificationListResponseDto> getNotificationsWithFilters(
            Long userId,
            com.devnest.notice.entity.NotificationType type,
            com.devnest.notice.entity.NotificationPriority priority,
            Boolean isRead,
            String keyword,
            Pageable pageable) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Page<Notification> notifications = notificationRepository
                .findWithFilters(userId, type, priority, isRead, keyword, pageable);

        return notifications.map(NotificationListResponseDto::from);
    }

    /**
     * 긴급 알림 조회
     */
    @Transactional(readOnly = true)
    public List<NotificationListResponseDto> getUrgentNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Notification> notifications = notificationRepository
                .findUrgentUnreadNotifications(userId);

        return notifications.stream()
                .map(NotificationListResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 알림 읽음/읽지 않음 상태 토글
     */
    @Transactional
    public Boolean toggleReadStatus(Long notificationId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));

        // 알림 소유자 확인
        if (!notification.getRecipient().getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 알림에 대한 권한이 없습니다.");
        }

        if (notification.getIsRead()) {
            notification.markAsUnread();
        } else {
            notification.markAsRead();
        }

        notificationRepository.save(notification);
        return notification.getIsRead();
    }

    /**
     * 특정 알림들을 소프트 삭제
     */
    @Transactional
    public Integer softDeleteNotifications(List<Long> notificationIds, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return notificationRepository.softDeleteByIds(notificationIds, userId, LocalDateTime.now());
    }

    /**
     * 모든 삭제 가능한 알림을 소프트 삭제
     */
    @Transactional
    public Integer softDeleteAllNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return notificationRepository.softDeleteAllByRecipientId(userId, LocalDateTime.now());
    }

    /**
     * 읽은 알림들만 소프트 삭제
     */
    @Transactional
    public Integer softDeleteReadNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return notificationRepository.softDeleteReadNotifications(userId, LocalDateTime.now());
    }

    /**
     * 삭제 가능한 알림 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<NotificationListResponseDto> getDeletableNotifications(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Page<Notification> notifications = notificationRepository
                .findDeletableNotifications(userId, pageable);

        return notifications.map(NotificationListResponseDto::from);
    }

    /**
     * 고도화된 알림 통계 조회
     */
    public NotificationStatsResponseDto getNotificationStatsAdvanced(Long userId) {
        if (!userRepository.existsById(userId)) {
            return NotificationStatsResponseDto.of(12, 45); // 더미 통계 데이터
        }

        long unreadCount = notificationRepository.countByRecipientUserIdAndIsReadFalseAndNotDeleted(userId);
        long totalCount = notificationRepository.countByRecipientUserIdAndNotDeleted(userId);

        return NotificationStatsResponseDto.of(unreadCount, totalCount);
    }

    /**
     * 신고 알림 생성
     */
    @Transactional
    public void createReportNotification(Long adminUserId, String reportTitle, Long reportId) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        Notification notification = com.devnest.notice.entity.Notification.createReportNotification(admin, reportTitle,
                reportId);
        notificationRepository.save(notification);
    }

    /**
     * 사용자 정지 알림 생성
     */
    @Transactional
    public void createSuspendNotification(Long userId, String reason, LocalDateTime until) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Notification notification = com.devnest.notice.entity.Notification.createSuspendNotification(user, reason,
                until);
        notificationRepository.save(notification);
    }

    /**
     * 자동 삭제 시간이 지난 알림들 정리
     */
    @Transactional
    public Integer cleanupExpiredNotifications() {
        List<Notification> expiredNotifications = notificationRepository.findExpiredNotifications(LocalDateTime.now());

        for (Notification notification : expiredNotifications) {
            notification.softDelete();
        }

        if (!expiredNotifications.isEmpty()) {
            notificationRepository.saveAll(expiredNotifications);
        }

        return expiredNotifications.size();
    }

    // === 더미 데이터 생성 메서드들 ===

    /**
     * 더미 알림 데이터 생성 (페이징)
     */
    private Page<NotificationListResponseDto> createDummyNotifications(Pageable pageable) {
        List<NotificationListResponseDto> dummyList = new ArrayList<>();

        dummyList.add(NotificationListResponseDto.builder()
                .notificationId(1L)
                .type("ANSWER")
                .message("웹개발자님이 \"React Hooks 사용법\" 질문에 새로운 답변을 달았습니다.")
                .isRead(false)
                .createdAt(LocalDateTime.now().minusMinutes(5))
                .targetId(123L)
                .targetType("QUESTION")
                .build());

        dummyList.add(NotificationListResponseDto.builder()
                .notificationId(2L)
                .type("ACCEPT")
                .message("회원님의 답변이 채택되었습니다.")
                .isRead(false)
                .createdAt(LocalDateTime.now().minusHours(2))
                .targetId(456L)
                .targetType("ANSWER")
                .build());

        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dummyList.size());
        List<NotificationListResponseDto> pageContent = dummyList.subList(start, end);

        return new PageImpl<>(pageContent, pageable, dummyList.size());
    }

    /**
     * 더미 최근 알림 데이터 생성
     */
    private List<NotificationListResponseDto> createDummyRecentNotifications() {
        List<NotificationListResponseDto> dummyList = new ArrayList<>();

        dummyList.add(NotificationListResponseDto.builder()
                .notificationId(1L)
                .type("ANSWER")
                .message("새로운 답변이 등록되었습니다: \"React Hooks 사용법\"")
                .isRead(false)
                .createdAt(LocalDateTime.now().minusMinutes(5))
                .targetId(123L)
                .targetType("QUESTION")
                .build());

        dummyList.add(NotificationListResponseDto.builder()
                .notificationId(2L)
                .type("SYSTEM")
                .message("시스템 점검 안내")
                .isRead(true)
                .createdAt(LocalDateTime.now().minusHours(1))
                .targetId(null)
                .targetType("SYSTEM")
                .build());

        return dummyList;
    }

    /**
     * 더미 읽지 않은 알림 데이터 생성 (페이징)
     */
    private Page<NotificationListResponseDto> createDummyUnreadNotifications(Pageable pageable) {
        List<NotificationListResponseDto> dummyList = new ArrayList<>();

        dummyList.add(NotificationListResponseDto.builder()
                .notificationId(1L)
                .type("ANSWER")
                .message("웹개발자님이 \"React Hooks 사용법\" 질문에 새로운 답변을 달았습니다.")
                .isRead(false)
                .createdAt(LocalDateTime.now().minusMinutes(5))
                .targetId(123L)
                .targetType("QUESTION")
                .build());

        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dummyList.size());
        List<NotificationListResponseDto> pageContent = dummyList.subList(start, end);

        return new PageImpl<>(pageContent, pageable, dummyList.size());
    }
}