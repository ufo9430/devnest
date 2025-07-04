package com.devnest.notice.dto.response;

import com.devnest.notice.entity.Notification;
import com.devnest.notice.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationListResponseDto {

    private Long notificationId;
    private String type;
    private String senderNickname;
    private String message;
    private String title;
    private String targetType;
    private Long targetId;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private String timeAgo; // "5분 전", "1시간 전" 등

    public static NotificationListResponseDto from(Notification notification) {
        return NotificationListResponseDto.builder()
                .notificationId(notification.getNotificationId())
                .type(notification.getType().name().toLowerCase())
                .senderNickname(
                        notification.getSender() != null ? notification.getSender().getNickname() : null)
                .message(notification.getMessage())
                .title(notification.getTitle())
                .targetType(notification.getTargetType())
                .targetId(notification.getTargetId())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .timeAgo(calculateTimeAgo(notification.getCreatedAt()))
                .build();
    }

    private static String calculateTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();

        long minutes = ChronoUnit.MINUTES.between(createdAt, now);
        if (minutes < 1) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        }

        long hours = ChronoUnit.HOURS.between(createdAt, now);
        if (hours < 24) {
            return hours + "시간 전";
        }

        long days = ChronoUnit.DAYS.between(createdAt, now);
        if (days < 7) {
            return days + "일 전";
        }

        long weeks = days / 7;
        if (weeks < 4) {
            return weeks + "주 전";
        }

        long months = days / 30;
        if (months < 12) {
            return months + "개월 전";
        }

        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    // 알림 타입에 따른 아이콘 반환
    public String getTypeIcon() {
        switch (type) {
            case "answer":
                return "message-circle";
            case "accept":
                return "check-circle";
            case "recommend":
                return "thumbs-up";
            case "comment":
                return "message-square";
            case "mention":
                return "at-sign";
            case "follow":
                return "user-plus";
            case "system":
                return "bell";
            default:
                return "bell";
        }
    }
}