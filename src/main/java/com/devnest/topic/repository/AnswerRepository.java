package com.devnest.topic.repository;

import com.devnest.topic.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByTopicIdOrderByCreatedAtDesc(Long topicId);
    boolean existsByTopicIdAndIsAcceptedTrue(Long topicId);
}