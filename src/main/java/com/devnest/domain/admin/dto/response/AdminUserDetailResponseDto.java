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
public class AdminUserDetailResponseDto {

    private Long userId;
    private String nickname;
    private String email;
    private String profileImage;
    private Role role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private int postCount; // 게시글 수
    private int commentCount; // 댓글 수

    public static AdminUserDetailResponseDto from(User user, int postCount, int commentCount) {
        return AdminUserDetailResponseDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .postCount(postCount)
                .commentCount(commentCount)
                .build();
    }

    // 상태를 문자열로 반환하는 편의 메서드
    public String getStatusText() {
        return isActive ? "활성" : "정지";
    }

    // 권한을 문자열로 반환하는 편의 메서드
    public String getRoleText() {
        return role == Role.ADMIN ? "관리자" : "일반회원";
    }
}