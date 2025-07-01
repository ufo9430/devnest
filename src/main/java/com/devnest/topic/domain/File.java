package com.devnest.topic.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "file")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id; // 파일 고유 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private TargetType targetType; // 첨부 대상 타입

    @Column(name = "target_id", nullable = false)
    private Long targetId; // 첨부 대상 ID

    @Column(nullable = false)
    private String filename; // 원본 파일명

    @Column(nullable = false, length = 500)
    private String url; // 파일 저장 URL

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt; // 업로드 일시

    @Builder
    public File(TargetType targetType, Long targetId, String filename, String url) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.filename = filename;
        this.url = url;
    }

    public enum TargetType {
        TOPIC, ANSWER
    }
}