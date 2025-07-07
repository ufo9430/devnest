package com.devnest.topic.dto;

import com.devnest.topic.domain.Report;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReportResponseDto {
    private Long id;
    private Long reporterId;
    private Long targetId;
    private Report.TargetType reportType;
    private String reason;
    private LocalDateTime createdAt;

    public ReportResponseDto(Report report) {
        this.id = report.getId();
        this.reporterId = report.getReporterId();
        this.targetId = report.getTargetId();
        this.reportType = report.getTargetType();
        this.reason = report.getReason();
        this.createdAt = report.getCreatedAt();
    }
}