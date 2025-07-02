package com.devnest.board.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Answer {
    @Id
    private Long answerId;


}
