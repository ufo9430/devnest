package com.devnest.domain.report.dto.response;

import com.devnest.domain.report.entity.Report;
import com.devnest.domain.report.entity.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportListResponseDto {

    private Long reportId;
    private String reporterNickname;
    private String reportedUserNickname;
    private String content;
    private String targetType;
    private Long targetId;
    private ReportStatus status;
    private LocalDateTime createdAt;

    public static ReportListResponseDto from(Report report) {
        return ReportListResponseDto.builder()
                .reportId(report.getReportId())
                .reporterNickname(report.getReporter().getNickname())
                .reportedUserNickname(
                        report.getReportedUser() != null ? report.getReportedUser().getNickname() : null)
                .content(report.getContent())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .build();
    }

    // 상태를 문자열로 반환하는 편의 메서드
    public String getStatusText() {
        return status.getDescription();
    }
}