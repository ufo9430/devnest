package com.devnest.board.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tagId;

    @ManyToMany
    @JoinTable(name = "topic_tag",
    joinColumns = @JoinColumn(name = "tag_id"),
    inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private List<Topic> topics;

    private String name;
}
