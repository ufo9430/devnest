package com.devnest.topic.controller;

import com.devnest.auth.domain.CustomUserDetails;
import com.devnest.topic.dto.AnswerAcceptRequestDto;
import com.devnest.topic.dto.AnswerRequestDto;
import com.devnest.topic.dto.AnswerResponseDto;
import com.devnest.topic.service.AnswerService;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/answers")
public class AnswerController {

    private final AnswerService answerService;

    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<AnswerResponseDto>> getAnswersByTopicId(@PathVariable Long topicId) {
        return ResponseEntity.ok(answerService.getAnswersByTopicId(topicId));
    }

    @PostMapping
    public ResponseEntity<?> createAnswer(
            @RequestBody @Valid AnswerRequestDto requestDto,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        requestDto.setUserId(userId);

        String markdown = requestDto.getMarkdownContent();
        if (markdown == null) markdown = "";
        String htmlContent = markdownToHtml(markdown);
        requestDto.setContent(htmlContent);

        AnswerResponseDto response = answerService.createAnswer(requestDto);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "답변이 등록되었습니다.",
                "data", response
        ));
    }

    // 예시 변환 함수
    private String markdownToHtml(String markdown) {
        // flexmark-java 등 사용
        return FlexmarkHtmlConverter.builder().build().convert(markdown);
    }

    @PatchMapping("/{answerId}")
    public ResponseEntity<?> updateAnswer(
            @PathVariable Long answerId,
            @RequestBody AnswerRequestDto dto,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        answerService.updateAnswer(answerId, userId, dto.getContent());
        return ResponseEntity.ok(Map.of("success", true, "message", "답변이 수정되었습니다."));
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<?> deleteAnswer(
            @PathVariable Long answerId,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        answerService.deleteAnswer(answerId, userId);
        return ResponseEntity.ok(Map.of("success", true, "message", "답변이 삭제되었습니다."));
    }

    @PostMapping("/accept")
    public ResponseEntity<AnswerResponseDto> acceptAnswer(
            @RequestBody @Valid AnswerAcceptRequestDto requestDto,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        // 세션의 사용자 ID로 설정
        requestDto.setUserId(userId);

        return ResponseEntity.ok(answerService.acceptAnswer(requestDto));
    }

    @PostMapping("/unaccept")
    public ResponseEntity<AnswerResponseDto> unacceptAnswer(
            @RequestBody @Valid AnswerAcceptRequestDto requestDto,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        // 세션의 사용자 ID로 설정
        requestDto.setUserId(userId);

        return ResponseEntity.ok(answerService.unacceptAnswer(requestDto));
    }
}