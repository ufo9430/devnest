package com.devnest.user.dto.response;

import com.devnest.user.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserManagementResponseDto {
    private Long userId;
    private String email;
    private String nickname;
    private Role role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String profileImage;
    private Long totalPosts;
    private Long totalAnswers;
    private String lastActiveDate;
}