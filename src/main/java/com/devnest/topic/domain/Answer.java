package com.devnest.topic.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "answer")
public class Answer {

    // 고유 식별자 (AUTO_INCREMENT)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;  // 답변 고유 ID

    // 지연 로딩을 사용하여 성능을 최적화
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;  // 연관된 질문

    @Column(name = "user_id", nullable = false)
    private Long userId;  // 답변자 ID

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // 답변 내용

    @Column(name = "is_accepted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean accepted = false;  // 채택 여부

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 작성일시

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;  // 수정일시

    @Column(columnDefinition = "TEXT")
    private String markdownContent; // ToastUI Editor의 markdown 원본 저장

    @Builder
    public Answer(String content, Long userId, Topic topic, boolean accepted, String markdownContent) {
        this.content = content;
        this.userId = userId;
        this.topic = topic;
        this.accepted = accepted;
        this.markdownContent = markdownContent;
    }

    // 답변 내용 수정 메서드
    public void updateContent(String content) {
        this.content = content;
    }
}