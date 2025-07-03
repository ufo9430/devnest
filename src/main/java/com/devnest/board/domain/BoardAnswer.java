package com.devnest.board.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "answer")
public class BoardAnswer {
    @Id
    @Column(name = "answer_id")
    private Long answerId;


}
