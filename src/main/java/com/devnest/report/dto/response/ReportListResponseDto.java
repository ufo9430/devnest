package com.devnest.report.dto.response;

import com.devnest.report.entity.Report;
import com.devnest.report.entity.ReportStatus;
import com.devnest.report.entity.ReportTargetType;
import com.devnest.report.entity.ReportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportListResponseDto {

    private Long reportId;
    private Long reporterId;
    private String reporterNickname;
    private Long reportedUserId;
    private String reportedUserNickname;
    private ReportTargetType targetType;
    private Long targetId;
    private ReportType reportType;
    private String reportTypeDescription;
    private String title;
    private String content;
    private ReportStatus status;
    private String statusDescription;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String adminResponse;
    private Long processedBy;
    private String processedByNickname;

    public static ReportListResponseDto fromEntity(Report report) {
        ReportListResponseDto dto = new ReportListResponseDto();
        dto.reportId = report.getReportId();
        dto.reporterId = report.getReporterId();
        dto.reportedUserId = report.getReportedUserId();
        dto.targetType = report.getTargetType();
        dto.targetId = report.getTargetId();
        dto.reportType = report.getReportType();
        dto.reportTypeDescription = report.getReportType().getDescription();
        dto.title = report.getTitle();
        dto.content = report.getContent();
        dto.status = report.getStatus();
        dto.statusDescription = report.getStatus().getDescription();
        dto.createdAt = report.getCreatedAt();
        dto.processedAt = report.getProcessedAt();
        dto.adminResponse = report.getAdminResponse();
        dto.processedBy = report.getProcessedBy();
        return dto;
    }
}