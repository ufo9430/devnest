package com.devnest.topic.controller;

import com.devnest.auth.domain.CustomUserDetails;
import com.devnest.topic.domain.Vote.VoteType;
import com.devnest.topic.dto.VoteRequestDto;
import com.devnest.topic.dto.VoteResponseDto;
import com.devnest.topic.repository.VoteRepository;
import com.devnest.topic.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        requestDto.setUserId(userId);
        return ResponseEntity.ok(voteService.addVote(requestDto, VoteType.LIKE));
    }

    @PostMapping("/dislike")
    public ResponseEntity<Map<String, Object>> dislike(
            @RequestBody @Valid VoteRequestDto requestDto,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        requestDto.setUserId(userId);
        return ResponseEntity.ok(voteService.addVote(requestDto, VoteType.DISLIKE));
    }

    @GetMapping("/counts")
    public ResponseEntity<VoteResponseDto> getVoteCounts(
            @RequestParam Long targetId) {
        return ResponseEntity.ok(voteService.getVoteCounts(targetId));
    }
}