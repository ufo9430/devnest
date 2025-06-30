package com.devnest.domain.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportStatsResponseDto {

    private long totalReports; // 전체 신고 수
    private long pendingReports; // 대기 중인 신고 수
    private long approvedReports; // 승인된 신고 수
    private long rejectedReports; // 반려된 신고 수
    private long todayReports; // 오늘 접수된 신고 수
}