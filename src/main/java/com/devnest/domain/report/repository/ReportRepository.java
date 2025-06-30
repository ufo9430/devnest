package com.devnest.domain.report.repository;

import com.devnest.domain.report.entity.Report;
import com.devnest.domain.report.entity.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // 상태별 신고 조회
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    // 신고자별 신고 조회
    Page<Report> findByReporter_Nickname(String nickname, Pageable pageable);

    // 신고 내용으로 검색
    Page<Report> findByContentContaining(String content, Pageable pageable);

    // 상태별 개수 조회
    long countByStatus(ReportStatus status);

    // 오늘 접수된 신고 개수
    long countByCreatedAtAfter(LocalDateTime dateTime);

    // 대기 중인 신고 목록 (알림용)
    List<Report> findTop10ByStatusOrderByCreatedAtDesc(ReportStatus status);

    // 복합 검색 (신고자 + 상태)
    @Query("SELECT r FROM Report r JOIN FETCH r.reporter " +
            "WHERE (:reporterNickname IS NULL OR r.reporter.nickname LIKE %:reporterNickname%) " +
            "AND (:status IS NULL OR r.status = :status)")
    Page<Report> findReportsWithConditions(
            @Param("reporterNickname") String reporterNickname,
            @Param("status") ReportStatus status,
            Pageable pageable);
}