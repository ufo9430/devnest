package com.devnest.topic.dto;

import lombok.Getter;

@Getter
public class VoteResponseDto {
    private final int likeCount;
    private final int dislikeCount;
    private final String message;

    public VoteResponseDto(int likeCount, int dislikeCount, String message) {
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.message = message;
    }
}