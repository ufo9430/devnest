package com.devnest.topic.controller;

import com.devnest.topic.domain.Vote.VoteType;
import com.devnest.topic.dto.VoteRequestDto;
import com.devnest.topic.dto.VoteResponseDto;
import com.devnest.topic.repository.VoteRepository;
import com.devnest.topic.service.VoteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/votes")
public class VoteController {

    private final VoteService voteService;
    private final VoteRepository voteRepository;

    @PostMapping("/like")
    public ResponseEntity<Map<String, Object>> like(
            @RequestBody @Valid VoteRequestDto requestDto,
            HttpSession session) {
        Long currentUserId = (Long) session.getAttribute("userId");
        currentUserId = 1L;
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "로그인 후 이용해 주세요."));
        }
        requestDto.setUserId(currentUserId);
        return ResponseEntity.ok(voteService.addVote(requestDto, VoteType.LIKE));
    }

    @PostMapping("/dislike")
    public ResponseEntity<Map<String, Object>> dislike(
            @RequestBody @Valid VoteRequestDto requestDto,
            HttpSession session) {
        Long currentUserId = (Long) session.getAttribute("userId");
        currentUserId = 1L;
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "로그인 후 이용해 주세요."));
        }
        requestDto.setUserId(currentUserId);
        return ResponseEntity.ok(voteService.addVote(requestDto, VoteType.DISLIKE));
    }

    @GetMapping("/counts")
    public ResponseEntity<VoteResponseDto> getVoteCounts(
            @RequestParam Long targetId) {
        return ResponseEntity.ok(voteService.getVoteCounts(targetId));
    }
}