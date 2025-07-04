package com.devnest.notice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemNotificationRequestDto {
    private Long userId;
    private String message;
    private String title;
}