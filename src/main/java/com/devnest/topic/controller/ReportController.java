package com.devnest.topic.controller;

import com.devnest.topic.dto.ReportRequestDto;
import com.devnest.topic.dto.ReportResponseDto;
import com.devnest.topic.service.ReportService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createReport(
            @RequestBody @Valid ReportRequestDto requestDto,
            HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            // 비로그인 처리 (테스트용)
            currentUserId = 1L;
        }

        // 신고 사유가 비어있는지 체크
        if (requestDto.getReason() == null || requestDto.getReason().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "신고 사유가 필요합니다."));
        }

        // 정상 신고 생성
        reportService.createReport(requestDto, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "신고가 접수되었습니다."));
    }
}