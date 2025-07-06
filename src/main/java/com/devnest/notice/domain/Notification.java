package com.devnest.notice.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private Long reciever_id;

    private String message;

    @Enumerated(value = EnumType.STRING)
    private Type type;

    private boolean is_read;

    private LocalDateTime created_at;
}
