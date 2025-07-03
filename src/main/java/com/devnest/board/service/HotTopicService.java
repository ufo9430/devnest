package com.devnest.board.service;

import com.devnest.board.domain.HotTopic;
import com.devnest.board.repository.HotTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotTopicService {

    private final HotTopicRepository hotTopicRepository;

    @Autowired
    public HotTopicService(HotTopicRepository hotTopicRepository) {
        this.hotTopicRepository = hotTopicRepository;
    }

    public List<HotTopic> getRecentFiveHotTopics(){
        return hotTopicRepository.findRecentFiveHotTopics();
    }
}
