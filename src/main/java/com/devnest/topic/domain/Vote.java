package com.devnest.topic.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// @UniqueConstraint를 사용하여 user_id, target_id, type의 조합이 고유하도록 설정
// 한 사용자가 같은 대상에 대해 추천과 비추천을 각각 한 번씩 할 수 있도록 변경
// VoteType Enum을 사용하여 LIKE/DISLIKE 타입 관리
// @CreationTimestamp를 사용하여 생성일시 자동 관리
// @NoArgsConstructor(access = AccessLevel.PROTECTED)로 기본 생성자 제한

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "vote", uniqueConstraints = {@UniqueConstraint(name = "unique_vote", columnNames = {"user_id", "target_id", "type"})})
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long id;  // 추천 고유 ID

    @Column(name = "user_id", nullable = false)
    private Long userId;  // 사용자 ID

    @Column(name = "target_id", nullable = false)
    private Long targetId;  // 추천 대상 ID (Topic 또는 Answer의 ID)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType type;  // 추천 타입

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 추천일시

    @Builder
    public Vote(Long userId, Long targetId, VoteType type) {
        this.userId = userId;
        this.targetId = targetId;
        this.type = type;
    }

    // 추천 타입을 나타내는 Enum
    public enum VoteType {
        LIKE, DISLIKE;

        @JsonCreator
        public static VoteType from(String value) {
            if (value == null) return null;
            return VoteType.valueOf(value.trim().toUpperCase());
        }
    }
}