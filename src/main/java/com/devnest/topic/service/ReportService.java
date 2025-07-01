package com.devnest.topic.service;

import com.devnest.topic.domain.Report;
import com.devnest.topic.dto.ReportRequestDto;
import com.devnest.topic.dto.ReportResponseDto;
import com.devnest.topic.repository.ReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    @Transactional
    public ReportResponseDto createReport(ReportRequestDto requestDto, Long reporterId) {
        // 이미 신고한 내역이 있는지 확인
        if (reportRepository.existsByReporterIdAndTargetId(reporterId, requestDto.getTargetId())) {
            throw new IllegalStateException("이미 신고하신 내용이 있습니다.");
        }

        Report report = Report.builder()
                .reporterId(reporterId)
                .targetId(requestDto.getTargetId())
                .targetType(requestDto.getTargetType())
                .reason(requestDto.getReason())
                .build();

        return new ReportResponseDto(reportRepository.save(report));
    }
}