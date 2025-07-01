package com.devnest.topic.controller;

import com.devnest.topic.dto.TopicRequestDto;
import com.devnest.topic.dto.TopicResponseDto;
import com.devnest.topic.service.TopicService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/topics")
public class TopicController {

    private final TopicService topicService;

    /**
     * 새로운 질문을 생성합니다.
     */
    @PostMapping("/add")
    public ResponseEntity<TopicResponseDto> createTopic(
            @RequestBody @Valid TopicRequestDto requestDto,
            HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            // Swagger 테스트 등 비로그인 상황에서 임시 userId 부여
//            currentUserId = 1L;
//            session.setAttribute("userId", currentUserId);

            throw new IllegalStateException("로그인이 필요합니다.");
        }

        return ResponseEntity.ok(topicService.createTopic(requestDto, currentUserId));
    }

    /**
     * 질문 상세 정보를 조회합니다.
     */

    @GetMapping("/{topicId}")
    public ResponseEntity<TopicResponseDto> getTopic(@PathVariable Long topicId) {
        return ResponseEntity.ok(topicService.getDetailTopic(topicId));
    }

    /**
     * 질문을 수정합니다.
     */
    @PatchMapping("/{topicId}/update")
    public ResponseEntity<TopicResponseDto> updateTopic(
            @PathVariable Long topicId,
            @RequestBody @Valid TopicRequestDto requestDto,
            HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            // Swagger 테스트 등 비로그인 상황에서 임시 userId 부여
//            currentUserId = 1L;
//            session.setAttribute("userId", currentUserId);

            throw new IllegalStateException("로그인이 필요합니다.");
        }

        return ResponseEntity.ok(topicService.updateDetailTopic(topicId, requestDto, currentUserId));
    }

    /**
     * 질문을 삭제합니다.
     */
    @DeleteMapping("/{topicId}/remove")
    public ResponseEntity<Void> deleteTopic(
            @PathVariable Long topicId,
            HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            // Swagger 테스트 등 비로그인 상황에서 임시 userId 부여
//            currentUserId = 1L;
//            session.setAttribute("userId", currentUserId);

            throw new IllegalStateException("로그인이 필요합니다.");
        }

        topicService.deleteDetailTopic(topicId, currentUserId);
        return ResponseEntity.ok().build();
    }
}