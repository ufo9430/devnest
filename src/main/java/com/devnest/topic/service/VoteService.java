package com.devnest.topic.service;

import com.devnest.topic.domain.Vote;
import com.devnest.topic.domain.Vote.VoteType;
import com.devnest.topic.dto.VoteRequestDto;
import com.devnest.topic.dto.VoteResponseDto;
import com.devnest.topic.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;

    @Transactional
    public Map<String, Object> addVote(VoteRequestDto requestDto, VoteType type) {
        Long userId = requestDto.getUserId();
        Long targetId = requestDto.getTargetId();

        // 이미 해당 타입으로 투표했는지 확인
        boolean alreadyVoted = voteRepository.existsByUserIdAndTargetIdAndType(userId, targetId, type);
        if (alreadyVoted) {
            return Map.of(
                    "success", false,
                    "message", type == VoteType.LIKE ? "이미 추천하셨습니다." : "이미 비추천하셨습니다.",
                    "voteCount", voteRepository.countByTargetIdAndType(targetId, VoteType.LIKE)
                            - voteRepository.countByTargetIdAndType(targetId, VoteType.DISLIKE)
            );
        }

        // 반대 타입의 투표가 있으면 삭제 + flush
        VoteType oppositeType = (type == VoteType.LIKE) ? VoteType.DISLIKE : VoteType.LIKE;
        voteRepository.findByUserIdAndTargetIdAndType(userId, targetId, oppositeType)
                .ifPresent(vote -> {
                    voteRepository.delete(vote);
                    voteRepository.flush();
                });

        // 투표 저장
        Vote vote = Vote.builder()
                .userId(userId)
                .targetId(targetId)
                .type(type)
                .build();
        voteRepository.save(vote);

        int voteCount = voteRepository.countByTargetIdAndType(targetId, VoteType.LIKE)
                - voteRepository.countByTargetIdAndType(targetId, VoteType.DISLIKE);

        return Map.of(
                "success", true,
                "message", type == VoteType.LIKE ? "추천되었습니다." : "비추천되었습니다.",
                "voteCount", voteCount
        );
    }

    /**
     * 특정 대상의 투표 수를 조회합니다.
     */
    @Transactional(readOnly = true)
    public VoteResponseDto getVoteCounts(Long targetId) {
        int likeCount = voteRepository.countByTargetIdAndType(targetId, VoteType.LIKE);
        int dislikeCount = voteRepository.countByTargetIdAndType(targetId, VoteType.DISLIKE);

        return new VoteResponseDto(likeCount, dislikeCount, "투표 수 조회 완료");
    }
}