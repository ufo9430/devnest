package com.devnest.topic.domain;

import com.devnest.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// @Entity 어노테이션으로 JPA 엔티티로 지정
// @CreationTimestamp와 @UpdateTimestamp로 자동 날짜 관리
// 상태 관리를 위한 Enum 클래스
// Lombok을 활용한 보일러플레이트 코드 감소
// MySQL의 모든 컬럼 정의를 자바 클래스에 매핑

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "topic")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long id;  // 질문 고유 ID

    @Column(nullable = false, length = 255)
    private String title;  // 질문 제목

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // 질문 내용

    @Column(name = "view_count", columnDefinition = "INT DEFAULT 0")
    private int viewCount = 0;  // 조회수

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('WAITING', 'RESOLVED') DEFAULT 'WAITING'")
    private TopicStatus status = TopicStatus.WAITING;  // 질문 상태

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 작성일시

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;  // 수정일시

    @Column(columnDefinition = "TEXT")
    private String markdownContent; // ToastUI Editor의 markdown 원본 저장

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 질문 상태를 나타내는 Enum
    public enum TopicStatus {
        WAITING, RESOLVED
    }

    // 조회수 증가 메서드
    public void increaseViewCount() {
        this.viewCount++;
    }

    // 상태 업데이트 메서드
    public void update(String title, String content, TopicStatus status) {
        this.title = title;
        this.content = content;
        this.status = status;
    }

    // 답변 내용 수정 메서드
    public void updateContent(String content) {
        this.content = content;
    }

    @ManyToMany
    @JoinTable(name = "topic_tag",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    // 답변과의 양방향 관계를 위한 메서드
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Answer> answers = new ArrayList<>();

//    Answer와 Topic간의 다대일(Many-to-One) 관계가 설정됨
//    Topic에서 Answer 리스트를 조회 할 수 있음
//    CascadeType.ALL로 인해 Topic이 삭제되면 관련된 Answer도 함께 삭제됨
//    orphanRemoval = true로 설정하여 컬렉션에서 제거된 Answer는 자동으로 삭제됨
}
