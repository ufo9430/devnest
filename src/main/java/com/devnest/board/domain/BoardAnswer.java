package com.devnest.board.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class BoardAnswer {
    @Id
    @Column(name = "answer_id")
    private Long answerId;


}
