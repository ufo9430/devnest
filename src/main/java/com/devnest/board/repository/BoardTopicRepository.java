package com.devnest.board.repository;

import com.devnest.board.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardTopicRepository extends JpaRepository<Topic, Long> {
    @Query(value = "select * from topic order by created_at desc limit 5", nativeQuery = true)
    List<Topic> findNewestFiveTopics();
}
