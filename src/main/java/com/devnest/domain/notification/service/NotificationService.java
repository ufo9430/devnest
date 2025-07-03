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
import java.util.ArrayList;
import org.springframework.data.domain.PageImpl;

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
     * 더미 알림 목록 생성
     */
    private Page<NotificationListResponseDto> createDummyNotifications(Pageable pageable) {
        List<NotificationListResponseDto> dummyNotifications = new ArrayList<>();

        // 더미 알림 데이터 생성
        dummyNotifications.add(new NotificationListResponseDto(
                1L, "answer", "김개발자", "질문에 답변이 달렸습니다.", "Spring Boot 질문",
                "topic", 1L, false, LocalDateTime.now().minusMinutes(5), "5분 전"));

        dummyNotifications.add(new NotificationListResponseDto(
                2L, "accept", "시스템", "답변이 채택되었습니다.", "답변 채택",
                "answer", 2L, false, LocalDateTime.now().minusMinutes(30), "30분 전"));

        dummyNotifications.add(new NotificationListResponseDto(
                3L, "recommend", "이자바", "답변이 추천되었습니다.", "답변 추천",
                "answer", 3L, false, LocalDateTime.now().minusHours(1), "1시간 전"));

        dummyNotifications.add(new NotificationListResponseDto(
                4L, "comment", "박프론트", "댓글이 달렸습니다.", "댓글 알림",
                "topic", 4L, false, LocalDateTime.now().minusHours(2), "2시간 전"));

        dummyNotifications.add(new NotificationListResponseDto(
                5L, "mention", "최백엔드", "댓글에서 언급되었습니다.", "멘션 알림",
                "comment", 5L, false, LocalDateTime.now().minusHours(3), "3시간 전"));

        dummyNotifications.add(new NotificationListResponseDto(
                6L, "follow", "정풀스택", "새로운 팔로워가 생겼습니다.", "팔로우 알림",
                "user", 6L, false, LocalDateTime.now().minusDays(1), "1일 전"));

        dummyNotifications.add(new NotificationListResponseDto(
                7L, "system", "시스템", "새로운 기능이 추가되었습니다.", "시스템 알림",
                "system", null, false, LocalDateTime.now().minusDays(2), "2일 전"));

        return new PageImpl<>(dummyNotifications, pageable, dummyNotifications.size());
    }

    /**
     * 더미 최근 알림 목록 생성
     */
    private List<NotificationListResponseDto> createDummyRecentNotifications() {
        List<NotificationListResponseDto> dummyNotifications = new ArrayList<>();

        dummyNotifications.add(new NotificationListResponseDto(
                1L, "answer", "김개발자", "질문에 답변이 달렸습니다.", "Spring Boot 질문",
                "topic", 1L, false, LocalDateTime.now().minusMinutes(5), "5분 전"));

        dummyNotifications.add(new NotificationListResponseDto(
                2L, "accept", "시스템", "답변이 채택되었습니다.", "답변 채택",
                "answer", 2L, false, LocalDateTime.now().minusMinutes(30), "30분 전"));

        dummyNotifications.add(new NotificationListResponseDto(
                3L, "recommend", "이자바", "답변이 추천되었습니다.", "답변 추천",
                "answer", 3L, false, LocalDateTime.now().minusHours(1), "1시간 전"));

        dummyNotifications.add(new NotificationListResponseDto(
                4L, "comment", "박프론트", "댓글이 달렸습니다.", "댓글 알림",
                "topic", 4L, false, LocalDateTime.now().minusHours(2), "2시간 전"));

        dummyNotifications.add(new NotificationListResponseDto(
                5L, "mention", "최백엔드", "댓글에서 언급되었습니다.", "멘션 알림",
                "comment", 5L, false, LocalDateTime.now().minusHours(3), "3시간 전"));

        return dummyNotifications;
    }

    /**
     * 더미 읽지 않은 알림 목록 생성
     */
    private Page<NotificationListResponseDto> createDummyUnreadNotifications(Pageable pageable) {
        List<NotificationListResponseDto> dummyNotifications = new ArrayList<>();

        // 읽지 않은 더미 알림 데이터 생성
        dummyNotifications.add(new NotificationListResponseDto(
                1L, "answer", "김개발자", "질문에 답변이 달렸습니다.", "Spring Boot 질문",
                "topic", 1L, false, LocalDateTime.now().minusMinutes(5), "5분 전"));

        dummyNotifications.add(new NotificationListResponseDto(
                2L, "accept", "시스템", "답변이 채택되었습니다.", "답변 채택",
                "answer", 2L, false, LocalDateTime.now().minusMinutes(30), "30분 전"));

        dummyNotifications.add(new NotificationListResponseDto(
                3L, "recommend", "이자바", "답변이 추천되었습니다.", "답변 추천",
                "answer", 3L, false, LocalDateTime.now().minusHours(1), "1시간 전"));

        return new PageImpl<>(dummyNotifications, pageable, dummyNotifications.size());
    }
}