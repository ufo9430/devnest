package com.devnest.topic.repository;

import com.devnest.topic.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "TopicTopicRepository")
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByUserIdOrderByCreatedAtDesc(Long userId);
    // 추가적인 쿼리 메서드가 필요하면 여기에 정의
}