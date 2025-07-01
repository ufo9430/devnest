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
    public VoteResponseDto addVote(VoteRequestDto requestDto, VoteType voteType) {
        // 이미 해당 타입으로 투표했는지 확인
        boolean alreadyVoted = voteRepository.existsByUserIdAndTargetIdAndType(
                requestDto.getUserId(),
                requestDto.getTargetId(),
                voteType
        );

        String message;
        if (alreadyVoted) {
            String voteTypeText = voteType == VoteType.LIKE ? "추천" : "비추천";
            message = "이미 " + voteTypeText + "하셨습니다.";
        } else {
            // 반대 타입의 투표가 있는지 확인
            VoteType oppositeType = (voteType == VoteType.LIKE) ? VoteType.DISLIKE : VoteType.LIKE;
            voteRepository.findByUserIdAndTargetIdAndType(
                    requestDto.getUserId(),
                    requestDto.getTargetId(),
                    oppositeType
            ).ifPresent(voteRepository::delete);

            // 새로운 투표 추가
            Vote vote = Vote.builder()
                    .userId(requestDto.getUserId())
                    .targetId(requestDto.getTargetId())
                    .type(voteType)
                    .build();
            voteRepository.save(vote);

            String voteTypeText = voteType == VoteType.LIKE ? "추천" : "비추천";
            message = voteTypeText + "되었습니다.";
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

        return new VoteResponseDto(likeCount, dislikeCount, message);
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