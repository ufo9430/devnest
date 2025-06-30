package com.devnest.domain.report.service;

import com.devnest.domain.report.entity.Report;
import com.devnest.domain.report.entity.ReportStatus;
import com.devnest.user.domain.User;
import com.devnest.domain.report.dto.response.ReportDetailResponseDto;
import com.devnest.domain.report.dto.response.ReportListResponseDto;
import com.devnest.domain.report.dto.response.ReportStatsResponseDto;
import com.devnest.domain.report.repository.ReportRepository;
import com.devnest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    /**
     * 신고 통계 조회
     */
    public ReportStatsResponseDto getReportStats() {
        long totalReports = reportRepository.count();
        long pendingReports = reportRepository.countByStatus(ReportStatus.PENDING);
        long approvedReports = reportRepository.countByStatus(ReportStatus.APPROVED);
        long rejectedReports = reportRepository.countByStatus(ReportStatus.REJECTED);
        long todayReports = reportRepository.countByCreatedAtAfter(
                LocalDateTime.now().minusDays(1));

        return ReportStatsResponseDto.builder()
                .totalReports(totalReports)
                .pendingReports(pendingReports)
                .approvedReports(approvedReports)
                .rejectedReports(rejectedReports)
                .todayReports(todayReports)
                .build();
    }

    /**
     * 신고 목록 조회 (검색, 필터링)
     */
    public Page<ReportListResponseDto> getReportList(
            String search, String status, Pageable pageable) {

        ReportStatus reportStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                reportStatus = ReportStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 잘못된 상태값은 무시
            }
        }

        Page<Report> reports = reportRepository.findReportsWithConditions(
                search, reportStatus, pageable);

        return reports.map(ReportListResponseDto::from);
    }

    /**
     * 신고 상세 조회
     */
    public ReportDetailResponseDto getReportDetail(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고입니다."));

        return ReportDetailResponseDto.from(report);
    }

    /**
     * 신고 승인 처리
     */
    @Transactional
    public Boolean approveReport(Long reportId, Long adminUserId, String adminNote) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고입니다."));

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        report.setStatus(ReportStatus.APPROVED);
        report.setProcessedAt(LocalDateTime.now());
        report.setProcessedBy(admin);
        report.setAdminNote(adminNote);

        reportRepository.save(report);
        return true;
    }

    /**
     * 신고 반려 처리
     */
    @Transactional
    public Boolean rejectReport(Long reportId, Long adminUserId, String adminNote) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고입니다."));

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        report.setStatus(ReportStatus.REJECTED);
        report.setProcessedAt(LocalDateTime.now());
        report.setProcessedBy(admin);
        report.setAdminNote(adminNote);

        reportRepository.save(report);
        return true;
    }

    /**
     * 일괄 승인 처리
     */
    @Transactional
    public Integer bulkApproveReports(List<Long> reportIds, Long adminUserId) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        int processedCount = 0;
        for (Long reportId : reportIds) {
            try {
                Report report = reportRepository.findById(reportId).orElse(null);
                if (report != null && report.getStatus() == ReportStatus.PENDING) {
                    report.setStatus(ReportStatus.APPROVED);
                    report.setProcessedAt(LocalDateTime.now());
                    report.setProcessedBy(admin);
                    reportRepository.save(report);
                    processedCount++;
                }
            } catch (Exception e) {
                // 개별 처리 실패는 무시하고 계속 진행
            }
        }

        return processedCount;
    }

    /**
     * 대기 중인 신고 개수 조회 (알림용)
     */
    public long getPendingReportsCount() {
        return reportRepository.countByStatus(ReportStatus.PENDING);
    }

    /**
     * 최근 대기 중인 신고 목록 (대시보드용)
     */
    public List<ReportListResponseDto> getRecentPendingReports() {
        List<Report> reports = reportRepository.findTop10ByStatusOrderByCreatedAtDesc(
                ReportStatus.PENDING);

        return reports.stream()
                .map(ReportListResponseDto::from)
                .toList();
    }
}