package com.devnest.topic.repository;

import com.devnest.topic.domain.TopicTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value = "TopicTopicTagRepository")
public interface TopicTagRepository extends JpaRepository<TopicTag, Long> {
    // 필요하다면 findByTopicId, findByTagId 등 추가 가능
}