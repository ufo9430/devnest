package com.devnest.board.domain;


import com.devnest.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long topicId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private String content;

    private Integer viewCount;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToMany
    @JoinTable(name = "topic_tag",
    joinColumns = @JoinColumn(name = "tag_id"),
    inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private List<BoardTag> tags;

    @OneToMany
    @JoinColumn(name = "answer_id")
    private List<BoardAnswer> answers;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
