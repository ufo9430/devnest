package com.devnest.report.controller;

import com.devnest.report.dto.request.ReportCreateRequestDto;
import com.devnest.report.dto.request.ReportProcessRequestDto;
import com.devnest.report.dto.response.ReportListResponseDto;
import com.devnest.report.dto.response.ReportStatsResponseDto;
import com.devnest.report.entity.ReportStatus;
import com.devnest.report.entity.ReportTargetType;
import com.devnest.report.entity.ReportType;
import com.devnest.report.service.ReportService;
import com.devnest.user.dto.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 신고 관련 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 신고 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createReport(
            @RequestParam Long reporterId,
            @Valid @RequestBody ReportCreateRequestDto requestDto) {

        try {
            Long reportId = reportService.createReport(reporterId, requestDto);
            return ResponseEntity.ok(ApiResponse.success("신고가 성공적으로 접수되었습니다.", reportId));
        } catch (Exception e) {
            log.error("신고 생성 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("신고 접수 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 신고 처리 (관리자 전용)
     */
    @PostMapping("/{reportId}/process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> processReport(
            @PathVariable Long reportId,
            @RequestParam Long adminId,
            @Valid @RequestBody ReportProcessRequestDto requestDto) {

        try {
            reportService.processReport(reportId, adminId, requestDto);
            return ResponseEntity.ok(ApiResponse.success("신고가 성공적으로 처리되었습니다.", null));
        } catch (Exception e) {
            log.error("신고 처리 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("신고 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 신고 목록 조회 (관리자 전용)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReportListResponseDto>>> getReports(
            @PageableDefault(size = 20) Pageable pageable) {

        try {
            Page<ReportListResponseDto> reports = reportService.getReports(pageable);
            return ResponseEntity.ok(ApiResponse.success("신고 목록 조회 성공", reports));
        } catch (Exception e) {
            log.error("신고 목록 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("신고 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 상태별 신고 목록 조회 (관리자 전용)
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReportListResponseDto>>> getReportsByStatus(
            @PathVariable ReportStatus status,
            @PageableDefault(size = 20) Pageable pageable) {

        try {
            Page<ReportListResponseDto> reports = reportService.getReportsByStatus(status, pageable);
            return ResponseEntity.ok(ApiResponse.success("상태별 신고 목록 조회 성공", reports));
        } catch (Exception e) {
            log.error("상태별 신고 목록 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("상태별 신고 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 신고 검색 (관리자 전용)
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReportListResponseDto>>> searchReports(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) ReportType reportType,
            @RequestParam(required = false) ReportTargetType targetType,
            @PageableDefault(size = 20) Pageable pageable) {

        try {
            Page<ReportListResponseDto> reports = reportService.searchReports(
                    keyword, status, reportType, targetType, pageable);
            return ResponseEntity.ok(ApiResponse.success("신고 검색 성공", reports));
        } catch (Exception e) {
            log.error("신고 검색 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("신고 검색 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 신고 상세 조회 (관리자 전용)
     */
    @GetMapping("/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReportListResponseDto>> getReport(
            @PathVariable Long reportId) {

        try {
            ReportListResponseDto report = reportService.getReport(reportId);
            return ResponseEntity.ok(ApiResponse.success("신고 상세 조회 성공", report));
        } catch (Exception e) {
            log.error("신고 상세 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("신고 상세 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 사용자가 신고한 내역 조회
     */
    @GetMapping("/reporter/{reporterId}")
    public ResponseEntity<ApiResponse<Page<ReportListResponseDto>>> getReportsByReporter(
            @PathVariable Long reporterId,
            @PageableDefault(size = 20) Pageable pageable) {

        try {
            Page<ReportListResponseDto> reports = reportService.getReportsByReporter(reporterId, pageable);
            return ResponseEntity.ok(ApiResponse.success("신고 내역 조회 성공", reports));
        } catch (Exception e) {
            log.error("신고 내역 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("신고 내역 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 사용자가 신고당한 내역 조회 (관리자 전용)
     */
    @GetMapping("/reported-user/{reportedUserId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReportListResponseDto>>> getReportsByReportedUser(
            @PathVariable Long reportedUserId,
            @PageableDefault(size = 20) Pageable pageable) {

        try {
            Page<ReportListResponseDto> reports = reportService.getReportsByReportedUser(reportedUserId, pageable);
            return ResponseEntity.ok(ApiResponse.success("피신고 내역 조회 성공", reports));
        } catch (Exception e) {
            log.error("피신고 내역 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("피신고 내역 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 최근 신고 조회 (관리자 대시보드용)
     */
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReportListResponseDto>>> getRecentReports() {

        try {
            List<ReportListResponseDto> reports = reportService.getRecentReports();
            return ResponseEntity.ok(ApiResponse.success("최근 신고 조회 성공", reports));
        } catch (Exception e) {
            log.error("최근 신고 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("최근 신고 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 신고 통계 조회 (관리자 전용)
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReportStatsResponseDto>> getReportStats() {

        try {
            ReportStatsResponseDto stats = reportService.getReportStats();
            return ResponseEntity.ok(ApiResponse.success("신고 통계 조회 성공", stats));
        } catch (Exception e) {
            log.error("신고 통계 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("신고 통계 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 미처리 신고 수 조회 (관리자 전용)
     */
    @GetMapping("/pending-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getPendingReportsCount() {

        try {
            long count = reportService.getPendingReportsCount();
            return ResponseEntity.ok(ApiResponse.success("미처리 신고 수 조회 성공", count));
        } catch (Exception e) {
            log.error("미처리 신고 수 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("미처리 신고 수 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}