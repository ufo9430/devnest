package com.devnest.domain.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class NotificationSecurityConfig {
    // 우리가 만든 NotificationController의 @PreAuthorize 애노테이션 활성화
}