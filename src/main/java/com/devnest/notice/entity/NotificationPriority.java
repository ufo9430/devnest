package com.devnest.notice.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationPriority {
    LOW("낮음", 1),
    NORMAL("보통", 2),
    HIGH("높음", 3),
    URGENT("긴급", 4);

    private final String description;
    private final int level;
}