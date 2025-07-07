package com.devnest.topic.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TopicTopicTag")
@Table(name = "topic_tag")
@Getter
@NoArgsConstructor
public class TopicTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public TopicTag(Topic topic, Tag tag) {
        this.topic = topic;
        this.tag = tag;
    }
}