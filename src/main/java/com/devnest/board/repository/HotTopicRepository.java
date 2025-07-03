package com.devnest.board.repository;

import com.devnest.board.domain.HotTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotTopicRepository extends JpaRepository<HotTopic, Long> {
    @Query(value = "select * from hot_topic order by snapshot_time desc limit 5", nativeQuery = true)
    List<HotTopic> findRecentFiveHotTopics();
}
