package com.devnest.topic.repository;

import com.devnest.topic.domain.File;
import com.devnest.topic.domain.File.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE File f SET f.targetId = :targetId WHERE f.url IN :urls AND f.targetType = :targetType")
    void updateTargetIdByUrls(@Param("targetId") Long targetId, @Param("urls") List<String> urls, @Param("targetType") File.TargetType targetType);

    List<File> findByTargetTypeAndTargetId(File.TargetType targetType, Long targetId);
}