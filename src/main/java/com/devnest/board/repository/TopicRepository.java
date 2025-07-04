package com.devnest.board.repository;

import com.devnest.board.domain.Status;
import com.devnest.board.domain.BoardTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
            "WHERE (title LIKE CONCAT('%', :keyword, '%') OR content LIKE CONCAT('%', :keyword, '%')) " +
            "AND status = 'RESOLVED'"
            , nativeQuery = true)
    Page<BoardTopic> searchSolvedByKeyword(Pageable pageable, String keyword);

    @Query("SELECT t FROM BoardTopic t JOIN t.tags tag WHERE tag.name = :tagName")
    Page<BoardTopic> searchByTag(Pageable pageable, @Param("tagName") String tag);

    @Query("SELECT t FROM BoardTopic t JOIN t.tags tag WHERE tag.name = :tagName AND t.status = 'RESOLVED'")
    Page<BoardTopic> searchSolvedByTag(Pageable pageable, @Param("tagName") String tag);

    @Query("SELECT DISTINCT t FROM BoardTopic t " +
            "JOIN t.tags tag " +
            "WHERE (t.title LIKE %:keyword% OR t.content LIKE %:keyword%) " +
            "AND tag.name = :tag")
    Page<BoardTopic> searchByKeywordAndTag(Pageable pageable,
                                           @Param("keyword") String keyword,
                                           @Param("tag") String tag);

    @Query("SELECT DISTINCT t FROM BoardTopic t " +
            "JOIN t.tags tag " +
            "WHERE ((t.title LIKE %:keyword% OR t.content LIKE %:keyword%) " +
            "AND tag.name = :tag)" +
            "AND status = 'RESOLVED'")
    Page<BoardTopic> searchSolvedByKeywordAndTag(Pageable pageable,
                                           @Param("keyword") String keyword,
                                           @Param("tag") String tag);

    long countByStatus(Status status);
}
