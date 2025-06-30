package com.devnest.topic.controller;

import com.devnest.topic.dto.AnswerAcceptRequestDto;
import com.devnest.topic.dto.AnswerRequestDto;
import com.devnest.topic.dto.AnswerResponseDto;
import com.devnest.topic.service.AnswerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<AnswerResponseDto> createAnswer(
            @RequestBody @Valid AnswerRequestDto requestDto,
            HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        // 세션의 사용자 ID로 설정 (클라이언트에서 보낸 값이 아닌 세션의 값을 사용)
        requestDto.setUserId(currentUserId);

        return ResponseEntity.ok(answerService.createAnswer(requestDto));
    }

    @PutMapping("/{answerId}")
    public ResponseEntity<AnswerResponseDto> updateAnswer(
            @PathVariable Long answerId,
            @RequestBody @Valid AnswerRequestDto requestDto,
            HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        return ResponseEntity.ok(answerService.updateAnswer(answerId, requestDto, currentUserId));
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<Void> deleteAnswer(
            @PathVariable Long answerId,
            HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        answerService.deleteAnswer(answerId, currentUserId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept")
    public ResponseEntity<AnswerResponseDto> acceptAnswer(
            @RequestBody @Valid AnswerAcceptRequestDto requestDto,
            HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
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
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        // 세션의 사용자 ID로 설정
        requestDto.setUserId(currentUserId);

        return ResponseEntity.ok(answerService.unacceptAnswer(requestDto));
    }
}