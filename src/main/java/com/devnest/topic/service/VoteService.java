package com.devnest.topic.service;

import com.devnest.topic.domain.Vote;
import com.devnest.topic.domain.Vote.VoteType;
import com.devnest.topic.dto.VoteRequestDto;
import com.devnest.topic.dto.VoteResponseDto;
import com.devnest.topic.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;

    @Transactional
    public VoteResponseDto toggleVote(VoteRequestDto requestDto, VoteType voteType) {
        Vote existingVote = voteRepository
                .findByUserIdAndTargetId(
                        requestDto.getUserId(),
                        requestDto.getTargetId()
                )
                .orElse(null);

        if (existingVote != null) {
            // 이미 투표한 경우
            if (existingVote.getType() == voteType) {
                // 같은 타입의 투표를 다시 누른 경우 취소
                voteRepository.delete(existingVote);
            } else {
                // 다른 타입으로 변경
                existingVote.changeType(voteType);
                voteRepository.save(existingVote);
            }
        } else {
            // 새로운 투표
            Vote vote = Vote.builder()
                    .userId(requestDto.getUserId())
                    .targetId(requestDto.getTargetId())
                    .type(voteType)
                    .build();
            voteRepository.save(vote);
        }

        // 업데이트된 투표 수 조회
        int likeCount = voteRepository.countByTargetIdAndType(
                requestDto.getTargetId(),
                VoteType.LIKE
        );

        int dislikeCount = voteRepository.countByTargetIdAndType(
                requestDto.getTargetId(),
                VoteType.DISLIKE
        );

        String message = voteType == VoteType.LIKE ? "추천" : "비추천";
        message += existingVote != null && existingVote.getType() == voteType ? "이 취소되었습니다." : "되었습니다.";

        return new VoteResponseDto(likeCount, dislikeCount, message);
    }

    @Transactional(readOnly = true)
    public VoteResponseDto getVoteCounts(Long targetId) {
        int likeCount = voteRepository.countByTargetIdAndType(
                targetId,
                VoteType.LIKE
        );

        int dislikeCount = voteRepository.countByTargetIdAndType(
                targetId,
                VoteType.DISLIKE
        );

        return new VoteResponseDto(likeCount, dislikeCount, "투표 수 조회 성공");
    }
}