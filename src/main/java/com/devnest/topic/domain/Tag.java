package com.devnest.topic.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TopicTag")
@Table(name = "tag")
@Getter
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    // 생성자
    public Tag(String name) {
        this.name = name;
    }
}