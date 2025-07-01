package com.devnest.board.domain;


import com.devnest.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int post_id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private String content;

    private int view_count;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;
}
