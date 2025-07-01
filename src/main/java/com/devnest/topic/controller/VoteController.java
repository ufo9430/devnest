package com.devnest.topic.controller;

import com.devnest.topic.domain.Vote.VoteType;
import com.devnest.topic.dto.VoteRequestDto;
import com.devnest.topic.dto.VoteResponseDto;
import com.devnest.topic.service.VoteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/votes")
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/like")
    public ResponseEntity<VoteResponseDto> like(
            @RequestBody @Valid VoteRequestDto requestDto,
            HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            currentUserId = -1L; // 비회원인 경우 -1로 설정 (임시)
        }

        requestDto.setUserId(currentUserId);
        return ResponseEntity.ok(voteService.addVote(requestDto, VoteType.LIKE));
    }

    @PostMapping("/dislike")
    public ResponseEntity<VoteResponseDto> dislike(
            @RequestBody @Valid VoteRequestDto requestDto,
            HttpSession session) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            currentUserId = -1L; // 비회원인 경우 -1로 설정 (임시)
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