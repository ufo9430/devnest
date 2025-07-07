package com.devnest.topic.repository;

import com.devnest.topic.domain.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    @Query(value = "select * from topic order by created_at desc limit 5", nativeQuery = true)
    List<Topic> findRecentFiveTopics();

    Page<Topic> findByStatus(Topic.TopicStatus status, Pageable pageable);

    Page<Topic> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query(value = "SELECT * " +
            "FROM topic " +
            "WHERE (title LIKE CONCAT('%', :keyword, '%') OR content LIKE CONCAT('%', :keyword, '%'))"
            , nativeQuery = true)
    Page<Topic> searchByKeyword(Pageable pageable, String keyword);

    @Query(value = "SELECT * " +
            "FROM topic " +
            "WHERE (title LIKE CONCAT('%', :keyword, '%') OR content LIKE CONCAT('%', :keyword, '%')) " +
            "AND status = 'RESOLVED'"
            , nativeQuery = true)
    Page<Topic> searchSolvedByKeyword(Pageable pageable, String keyword);

    @Query("SELECT t FROM Topic t JOIN t.tags tag WHERE tag.name = :tagName")
    Page<Topic> searchByTag(Pageable pageable, @Param("tagName") String tag);

    @Query("SELECT t FROM Topic t JOIN t.tags tag WHERE tag.name = :tagName AND t.status = 'RESOLVED'")
    Page<Topic> searchSolvedByTag(Pageable pageable, @Param("tagName") String tag);

    @Query("SELECT DISTINCT t FROM Topic t " +
            "JOIN t.tags tag " +
            "WHERE (t.title LIKE %:keyword% OR t.content LIKE %:keyword%) " +
            "AND tag.name = :tag")
    Page<Topic> searchByKeywordAndTag(Pageable pageable,
                                           @Param("keyword") String keyword,
                                           @Param("tag") String tag);

    @Query("SELECT DISTINCT t FROM Topic t " +
            "JOIN t.tags tag " +
            "WHERE ((t.title LIKE %:keyword% OR t.content LIKE %:keyword%) " +
            "AND tag.name = :tag)" +
            "AND status = 'RESOLVED'")
    Page<Topic> searchSolvedByKeywordAndTag(Pageable pageable,
                                           @Param("keyword") String keyword,
                                           @Param("tag") String tag);

    long countByStatus(Topic.TopicStatus status);
}