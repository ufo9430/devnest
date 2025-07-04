package com.devnest.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationStatsResponseDto {

    private long unreadCount; // 읽지 않은 알림 개수
    private long totalCount; // 전체 알림 개수
    private boolean hasUnread; // 읽지 않은 알림이 있는지 여부

    public static NotificationStatsResponseDto of(long unreadCount, long totalCount) {
        return NotificationStatsResponseDto.builder()
                .unreadCount(unreadCount)
                .totalCount(totalCount)
                .hasUnread(unreadCount > 0)
                .build();
    }
}