package com.devnest.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStatsResponseDto {

    private long totalUsers; // 전체 회원 수
    private long activeUsers; // 활성 회원 수
    private long suspendedUsers; // 정지된 회원 수
    private long todayNewUsers; // 오늘 신규 가입자 수
}