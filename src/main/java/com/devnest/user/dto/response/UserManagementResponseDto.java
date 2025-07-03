package com.devnest.user.dto.response;

import com.devnest.user.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String createdAt; // String으로 포맷된 날짜
}