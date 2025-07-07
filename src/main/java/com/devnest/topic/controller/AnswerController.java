package com.devnest.topic.controller;

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
            HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("userId");
        currentUserId = 1L; // 테스트용
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }
        requestDto.setUserId(currentUserId);

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
            HttpSession session) {
        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            // Swagger 테스트 등 비로그인 상황에서 임시 userId 부여
            currentUserId = 1L;
            session.setAttribute("userId", currentUserId);
            // throw new IllegalStateException("로그인이 필요합니다.");
        }

        answerService.updateAnswer(answerId, currentUserId, dto.getContent());
        return ResponseEntity.ok(Map.of("success", true, "message", "답변이 수정되었습니다."));
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<?> deleteAnswer(
            @PathVariable Long answerId,
            HttpSession session) {
        Long currentUserId = (Long) session.getAttribute("userId");
        // Swagger 테스트 등 비로그인 상황에서 임시 userId 부여
        currentUserId = 1L;
        // 권한 체크(작성자만 삭제 가능)
        answerService.deleteAnswer(answerId, currentUserId);
        return ResponseEntity.ok(Map.of("success", true, "message", "답변이 삭제되었습니다."));
    }

    @PostMapping("/accept")
    public ResponseEntity<AnswerResponseDto> acceptAnswer(
            @RequestBody @Valid AnswerAcceptRequestDto requestDto,
            HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            // Swagger 테스트 등 비로그인 상황에서 임시 userId 부여
            currentUserId = 1L;
            session.setAttribute("userId", currentUserId);
            // throw new IllegalStateException("로그인이 필요합니다.");
        }

        // 세션의 사용자 ID로 설정
        requestDto.setUserId(currentUserId);

        return ResponseEntity.ok(answerService.acceptAnswer(requestDto));
    }

    @PostMapping("/unaccept")
    public ResponseEntity<AnswerResponseDto> unacceptAnswer(
            @RequestBody @Valid AnswerAcceptRequestDto requestDto,
            HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            // Swagger 테스트 등 비로그인 상황에서 임시 userId 부여
            currentUserId = 1L;
            session.setAttribute("userId", currentUserId);
            // throw new IllegalStateException("로그인이 필요합니다.");
        }

        // 세션의 사용자 ID로 설정
        requestDto.setUserId(currentUserId);

        return ResponseEntity.ok(answerService.unacceptAnswer(requestDto));
    }
}