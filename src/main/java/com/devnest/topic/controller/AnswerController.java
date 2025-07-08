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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answers")
public class AnswerController {

    private final AnswerService answerService;

    @GetMapping("/topic/{topicId}")
    @ResponseBody
    public ResponseEntity<List<AnswerResponseDto>> getAnswersByTopicId(@PathVariable Long topicId) {
        return ResponseEntity.ok(answerService.getAnswersByTopicId(topicId));
    }

    @PostMapping
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
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

    @PostMapping("/{answerId}/accept")
    public String acceptAnswer(@PathVariable Long answerId, Authentication authentication, RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        try {
            answerService.acceptAnswer(answerId, userId);
            redirectAttributes.addFlashAttribute("successMessage", "답변이 채택되었습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        // 질문 상세 페이지로 리다이렉트 (answerId로 topicId를 조회해야 함)
        Long topicId = answerService.getTopicIdByAnswerId(answerId);
        return "redirect:/topics/" + topicId;
    }
}