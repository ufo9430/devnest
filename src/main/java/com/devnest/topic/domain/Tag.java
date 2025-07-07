package com.devnest.topic.domain;

import com.devnest.topic.domain.Topic;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "tag")
@Getter
@NoArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    @ManyToMany(mappedBy = "tags")
    private List<Topic> topics = new ArrayList<>();

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    public Tag(String name) {
        this.name = name;
    }
}