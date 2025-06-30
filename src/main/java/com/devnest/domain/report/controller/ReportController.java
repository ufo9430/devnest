package com.devnest.domain.report.controller;

import com.devnest.user.dto.common.ApiResponse;
import com.devnest.domain.report.dto.response.ReportDetailResponseDto;
import com.devnest.domain.report.dto.response.ReportListResponseDto;
import com.devnest.domain.report.dto.response.ReportStatsResponseDto;
import com.devnest.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final ReportService reportService;

    /**
     * 신고 통계 조회
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<ReportStatsResponseDto>> getReportStats() {
        try {
            ReportStatsResponseDto stats = reportService.getReportStats();
            return ResponseEntity.ok(ApiResponse.success("신고 통계 조회 성공", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("신고 통계 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 신고 목록 조회 (검색, 필터링, 페이징)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReportListResponseDto>>> getReportList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<ReportListResponseDto> reports = reportService.getReportList(
                    search, status, pageable);

            return ResponseEntity.ok(ApiResponse.success("신고 목록 조회 성공", reports));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("신고 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 신고 상세 조회
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<ReportDetailResponseDto>> getReportDetail(
            @PathVariable Long reportId) {
        try {
            ReportDetailResponseDto report = reportService.getReportDetail(reportId);
            return ResponseEntity.ok(ApiResponse.success("신고 상세 조회 성공", report));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("신고 상세 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 신고 승인 처리
     */
    @PatchMapping("/{reportId}/approve")
    public ResponseEntity<ApiResponse<Boolean>> approveReport(
            @PathVariable Long reportId,
            @RequestParam(required = false) String adminNote) {
        try {
            // TODO: 실제 구현에서는 인증된 관리자 ID를 가져와야 함
            Long adminUserId = 1L; // 임시 관리자 ID

            Boolean result = reportService.approveReport(reportId, adminUserId, adminNote);
            return ResponseEntity.ok(
                    ApiResponse.success("신고가 승인되었습니다.", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("신고 승인에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 신고 반려 처리
     */
    @PatchMapping("/{reportId}/reject")
    public ResponseEntity<ApiResponse<Boolean>> rejectReport(
            @PathVariable Long reportId,
            @RequestParam(required = false) String adminNote) {
        try {
            // TODO: 실제 구현에서는 인증된 관리자 ID를 가져와야 함
            Long adminUserId = 1L; // 임시 관리자 ID

            Boolean result = reportService.rejectReport(reportId, adminUserId, adminNote);
            return ResponseEntity.ok(
                    ApiResponse.success("신고가 반려되었습니다.", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("신고 반려에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 일괄 승인 처리
     */
    @PatchMapping("/bulk-approve")
    public ResponseEntity<ApiResponse<Integer>> bulkApproveReports(
            @RequestBody List<Long> reportIds) {
        try {
            // TODO: 실제 구현에서는 인증된 관리자 ID를 가져와야 함
            Long adminUserId = 1L; // 임시 관리자 ID

            Integer processedCount = reportService.bulkApproveReports(reportIds, adminUserId);
            return ResponseEntity.ok(
                    ApiResponse.success(processedCount + "개의 신고가 일괄 승인되었습니다.", processedCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("일괄 승인에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 대기 중인 신고 개수 조회 (알림용)
     */
    @GetMapping("/pending-count")
    public ResponseEntity<ApiResponse<Long>> getPendingReportsCount() {
        try {
            long count = reportService.getPendingReportsCount();
            return ResponseEntity.ok(ApiResponse.success("대기 중인 신고 개수 조회 성공", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("대기 중인 신고 개수 조회에 실패했습니다."));
        }
    }

    /**
     * 최근 대기 중인 신고 목록 (대시보드용)
     */
    @GetMapping("/recent-pending")
    public ResponseEntity<ApiResponse<List<ReportListResponseDto>>> getRecentPendingReports() {
        try {
            List<ReportListResponseDto> reports = reportService.getRecentPendingReports();
            return ResponseEntity.ok(ApiResponse.success("최근 신고 목록 조회 성공", reports));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("최근 신고 목록 조회에 실패했습니다."));
        }
    }
}