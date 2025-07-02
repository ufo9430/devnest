package com.devnest.board.service;

import com.devnest.board.domain.Topic;
import com.devnest.board.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {
    private final TopicRepository topicRepository;

    @Autowired
    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public List<Topic> getRecentFiveTopics(){
        return topicRepository.findRecentFiveTopics();
    }

    public List<Topic> getRecentTopics(){
        return topicRepository.findAll();
    }

}