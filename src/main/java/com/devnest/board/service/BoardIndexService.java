package com.devnest.board.service;

import com.devnest.board.domain.Topic;
import com.devnest.board.repository.BoardTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardIndexService {
    private final BoardTopicRepository topicRepository;

    @Autowired
    public BoardIndexService(BoardTopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public List<Topic> getRecentFiveTopics(){
        return topicRepository.findRecentFiveTopics();
    }

}