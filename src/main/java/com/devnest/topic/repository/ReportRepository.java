package com.devnest.topic.repository;

import com.devnest.topic.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporterIdAndTargetId(Long reporterId, Long targetId);
}