package com.devnest.topic.controller;

import com.devnest.topic.domain.Answer;
import com.devnest.topic.dto.AnswerResponseDto;
import com.devnest.topic.dto.TopicRequestDto;
import com.devnest.topic.dto.TopicResponseDto;
import com.devnest.topic.service.AnswerService;
import com.devnest.topic.service.TopicService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/topics")
public class TopicController {

    private static final Logger log = LoggerFactory.getLogger(TopicController.class);
    private final TopicService topicService;
    private final AnswerService answerService;

    @GetMapping("/post") // 임의 매핑한것
    public String showNewPostForm(Model model) {
        // 필요하다면 model에 값 추가
        return "post_new"; // src/main/resources/templates/post_new.html
    }

    /**
     * 새로운 질문을 생성합니다.
     */
    @PostMapping("/add")
    public String createTopic(
            @ModelAttribute @Valid TopicRequestDto requestDto,
            HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            // Swagger 테스트 등 비로그인 상황에서 임시 userId 부여
            currentUserId = 1L;
            session.setAttribute("userId", currentUserId);
            //throw new IllegalStateException("로그인이 필요합니다.");
        }

        // TopicRequestDto에 markdownContent가 포함되어 있어야 함
        topicService.createTopic(requestDto, currentUserId);
        return "redirect:/";
    }

    /**
     * 질문 상세 정보를 조회합니다.
     */

    @GetMapping("/{topicId}")
    public String getTopicDetail(@PathVariable Long topicId, Model model, HttpSession session) {
        TopicResponseDto topic = topicService.getDetailTopic(topicId);
        List<AnswerResponseDto> answers = answerService.getAnswersByTopicId(topicId);

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            // Swagger 테스트 등 비로그인 상황에서 임시 userId 부여
            currentUserId = 1L;
            session.setAttribute("userId", currentUserId);
            // throw new IllegalStateException("로그인이 필요합니다.");
        }

        Long finalCurrentUserId = currentUserId;
        boolean hasAnswered = answers.stream()
                .anyMatch(answer -> answer.getUserId().equals(finalCurrentUserId));

        System.out.println(topic.getImageUrls());

        model.addAttribute("hasAnswered", hasAnswered);
        model.addAttribute("topic", topic);
        model.addAttribute("answers", answers);
        model.addAttribute("currentUserId", currentUserId); // 임시로 userId 부여

        return "post_detail"; // src/main/resources/templates/post_detail.html로 이동
    }

    /**
     * 질문을 수정합니다.
     */
    @PatchMapping("/{topicId}/update")
    public ResponseEntity<?> updateTopic(
            @PathVariable Long topicId,
            @RequestBody @Valid TopicRequestDto requestDto,
            HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            currentUserId = 1L;
            session.setAttribute("userId", currentUserId);
        }

        TopicResponseDto updated = topicService.updateDetailTopic(topicId, requestDto, currentUserId);

        // 수정된 마크다운/HTML 등도 함께 반환하고 싶다면 아래처럼 응답
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "수정 완료",
                "content", updated.getContent() != null ? updated.getContent() : "",
                "markdownContent", updated.getMarkdownContent() != null ? updated.getMarkdownContent() : ""
        ));
    }

    /**
     * 질문을 삭제합니다.
     */
    @DeleteMapping("/{topicId}/remove")
    public String deleteTopic(
            @PathVariable Long topicId,
            HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            // Swagger 테스트 등 비로그인 상황에서 임시 userId 부여
           currentUserId = 1L;
            session.setAttribute("userId", currentUserId);

            // throw new IllegalStateException("로그인이 필요합니다.");
        }

        topicService.deleteDetailTopic(topicId, currentUserId);
        return "redirect:/";
    }
}