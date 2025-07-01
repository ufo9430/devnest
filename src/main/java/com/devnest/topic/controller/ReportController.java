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

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponseDto> createReport(
            @RequestBody @Valid ReportRequestDto requestDto,
            HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            // Swagger 테스트 등 비로그인 상황에서 임시 userId 부여
            currentUserId = 1L;
            session.setAttribute("userId", currentUserId);
            // throw new IllegalStateException("로그인이 필요합니다.");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reportService.createReport(requestDto, currentUserId));
    }
}