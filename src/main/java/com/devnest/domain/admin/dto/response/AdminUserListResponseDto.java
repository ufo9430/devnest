package com.devnest.domain.admin.dto.response;

import com.devnest.user.domain.Role;
import com.devnest.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserListResponseDto {

    private Long userId;
    private String nickname;
    private String email;
    private String profileImage;
    private Role role;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public static AdminUserListResponseDto from(User user) {
        return AdminUserListResponseDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    // 상태를 문자열로 반환하는 편의 메서드
    public String getStatusText() {
        return isActive ? "활성" : "정지";
    }
}