package com.devnest.topic.repository;

import com.devnest.topic.domain.Vote;
import com.devnest.topic.domain.Vote.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserIdAndTargetId(Long userId, Long targetId);
    int countByTargetIdAndType(Long targetId, VoteType type);
    boolean existsByUserIdAndTargetId(Long userId, Long targetId);
}