package com.devnest.topic.repository;

import com.devnest.topic.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByTopicIdOrderByCreatedAtDesc(Long topicId);
    boolean existsByTopicIdAndIsAcceptedTrue(Long topicId);
    // /board
    @Query(value = "SELECT COUNT(*) FROM answer WHERE DATE(created_at) = CURDATE()", nativeQuery = true)
    long countTodayCreated();
    @Query(value = """
    SELECT a.*
    FROM answer a
    LEFT JOIN (
        SELECT target_id,
               SUM(CASE WHEN type = 'LIKE' THEN 1 WHEN type = 'DISLIKE' THEN -1 ELSE 0 END) AS score
        FROM vote
        GROUP BY target_id
    ) v ON a.answer_id = v.target_id
    WHERE a.topic_id = :topicId
    ORDER BY COALESCE(v.score, 0) DESC, a.created_at DESC
    """, nativeQuery = true)
    List<Answer> findByTopicIdOrderByVoteScoreDesc(@Param("topicId") Long topicId);
}