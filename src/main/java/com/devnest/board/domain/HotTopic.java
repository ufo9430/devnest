package com.devnest.board.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class HotTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime snapshotTime;

    @OneToOne
    @JoinColumn(name = "topic_id")
    private BoardTopic topic;
}
