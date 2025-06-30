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
public class ReportDetailResponseDto {

    private Long reportId;
    private String reporterNickname;
    private String reporterEmail;
    private String reportedUserNickname;
    private String reportedUserEmail;
    private String content;
    private String targetType;
    private Long targetId;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String processedByNickname;
    private String adminNote;

    public static ReportDetailResponseDto from(Report report) {
        return ReportDetailResponseDto.builder()
                .reportId(report.getReportId())
                .reporterNickname(report.getReporter().getNickname())
                .reporterEmail(report.getReporter().getEmail())
                .reportedUserNickname(
                        report.getReportedUser() != null ? report.getReportedUser().getNickname() : null)
                .reportedUserEmail(
                        report.getReportedUser() != null ? report.getReportedUser().getEmail() : null)
                .content(report.getContent())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .processedAt(report.getProcessedAt())
                .processedByNickname(
                        report.getProcessedBy() != null ? report.getProcessedBy().getNickname() : null)
                .adminNote(report.getAdminNote())
                .build();
    }

    // 상태를 문자열로 반환하는 편의 메서드
    public String getStatusText() {
        return status.getDescription();
    }
}