package com.devnest.board.repository;

import com.devnest.board.domain.Status;
import com.devnest.board.domain.BoardTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<BoardTopic, Long> {
    @Query(value = "select * from topic order by created_at desc limit 5", nativeQuery = true)
    List<BoardTopic> findRecentFiveTopics();

    Page<BoardTopic> findByStatus(Status status, Pageable pageable);

    Page<BoardTopic> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query(value = "SELECT * " +
            "FROM topic " +
            "WHERE (title LIKE CONCAT('%', :keyword, '%') OR content LIKE CONCAT('%', :keyword, '%'))"
            , nativeQuery = true)
    Page<BoardTopic> searchByKeyword(Pageable pageable, String keyword);

    @Query(value = "SELECT * " +
            "FROM topic " +
            "WHERE (title LIKE CONCAT('%', :keyword, '%')) OR content LIKE CONCAT('%', :keyword, '%') " +
            "AND status = 'RESOLVED'"
            , nativeQuery = true)
    Page<BoardTopic> searchSolvedByKeyword(Pageable pageable, String keyword);

    long countByStatus(Status status);
}
