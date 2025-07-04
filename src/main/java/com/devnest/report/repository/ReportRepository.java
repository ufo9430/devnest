package com.devnest.report.repository;

import com.devnest.report.entity.Report;
import com.devnest.report.entity.ReportStatus;
import com.devnest.report.entity.ReportTargetType;
import com.devnest.report.entity.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // 처리 상태별 신고 조회
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    // 특정 사용자가 신고한 내역 조회
    Page<Report> findByReporterId(Long reporterId, Pageable pageable);

    // 특정 사용자가 신고당한 내역 조회
    Page<Report> findByReportedUserId(Long reportedUserId, Pageable pageable);

    // 신고 대상 유형별 조회
    Page<Report> findByTargetType(ReportTargetType targetType, Pageable pageable);

    // 신고 유형별 조회
    Page<Report> findByReportType(ReportType reportType, Pageable pageable);

    // 최근 신고 조회
    List<Report> findTop10ByOrderByCreatedAtDesc();

    // 특정 대상에 대한 신고 조회
    List<Report> findByTargetTypeAndTargetId(ReportTargetType targetType, Long targetId);

    // 신고 통계 조회
    @Query("SELECT COUNT(r) FROM Report r WHERE r.status = :status")
    long countByStatus(@Param("status") ReportStatus status);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.reportType = :reportType")
    long countByReportType(@Param("reportType") ReportType reportType);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.createdAt >= :startDate")
    long countByCreatedAtAfter(@Param("startDate") LocalDateTime startDate);

    // 미처리 신고 수 조회
    @Query("SELECT COUNT(r) FROM Report r WHERE r.status = 'PENDING'")
    long countPendingReports();

    // 특정 사용자가 신고당한 횟수 조회
    @Query("SELECT COUNT(r) FROM Report r WHERE r.reportedUserId = :userId AND r.status = 'APPROVED'")
    long countApprovedReportsByUserId(@Param("userId") Long userId);

    // 검색 기능
    @Query("SELECT r FROM Report r WHERE " +
            "(:keyword IS NULL OR r.title LIKE %:keyword% OR r.content LIKE %:keyword%) AND " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:reportType IS NULL OR r.reportType = :reportType) AND " +
            "(:targetType IS NULL OR r.targetType = :targetType)")
    Page<Report> searchReports(@Param("keyword") String keyword,
            @Param("status") ReportStatus status,
            @Param("reportType") ReportType reportType,
            @Param("targetType") ReportTargetType targetType,
            Pageable pageable);
}