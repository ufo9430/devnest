package com.devnest.board.repository;

import com.devnest.board.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardTagRepository extends JpaRepository<Tag, Long> {

}
