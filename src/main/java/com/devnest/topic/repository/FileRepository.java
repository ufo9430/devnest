package com.devnest.topic.repository;

import com.devnest.topic.domain.File;
import com.devnest.topic.domain.File.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByTargetTypeAndTargetId(TargetType targetType, Long targetId);
    void deleteByTargetTypeAndTargetId(TargetType targetType, Long targetId);
    List<File> findByTargetTypeAndTargetIdOrderByUploadedAtDesc(TargetType targetType, Long targetId);
}