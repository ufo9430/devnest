package com.devnest.topic.repository;

import com.devnest.topic.domain.Vote;
import com.devnest.topic.domain.Vote.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    // 특정 사용자의 특정 대상에 대한 특정 타입의 투표 조회
    Optional<Vote> findByUserIdAndTargetIdAndType(Long userId, Long targetId, VoteType type);

    // 특정 대상의 특정 타입 투표 수 조회
    int countByTargetIdAndType(Long targetId, VoteType type);

    // 특정 사용자가 특정 대상에 특정 타입으로 투표했는지 확인
    boolean existsByUserIdAndTargetIdAndType(Long userId, Long targetId, VoteType type);
}