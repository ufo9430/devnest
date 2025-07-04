package com.devnest.admin.dto.response;

import com.devnest.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSuspendResponseDto {

    private Long userId;
    private String nickname;
    private String email;
    private boolean suspended;
    private LocalDateTime suspendedUntil;
    private String suspendReason;
    private Long suspendedBy;
    private LocalDateTime suspendedAt;
    private String suspendedByNickname; // 정지 처리한 관리자 닉네임

    public static UserSuspendResponseDto fromEntity(User user) {
        UserSuspendResponseDto dto = new UserSuspendResponseDto();
        dto.userId = user.getUserId();
        dto.nickname = user.getNickname();
        dto.email = user.getEmail();
        dto.suspended = user.isSuspended();
        dto.suspendedUntil = user.getSuspendedUntil();
        dto.suspendReason = user.getSuspendReason();
        dto.suspendedBy = user.getSuspendedBy();
        dto.suspendedAt = user.getSuspendedAt();
        return dto;
    }
}