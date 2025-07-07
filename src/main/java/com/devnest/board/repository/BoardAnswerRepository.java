package com.devnest.board.repository;

import com.devnest.board.domain.BoardAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface BoardAnswerRepository extends JpaRepository<BoardAnswer, Long> {
    @Query(value = "SELECT COUNT(*) FROM topic WHERE DATE(created_at) = CURDATE()", nativeQuery = true)
    long countTodayCreated();
}
