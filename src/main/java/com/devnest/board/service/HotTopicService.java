package com.devnest.board.service;

import com.devnest.board.domain.HotTopic;
import com.devnest.board.dto.TopicResponseDTO;
import com.devnest.board.repository.HotTopicRepository;
import com.devnest.topic.domain.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HotTopicService {

    private final HotTopicRepository hotTopicRepository;

    @Autowired
    public HotTopicService(HotTopicRepository hotTopicRepository) {
        this.hotTopicRepository = hotTopicRepository;
    }

    public List<TopicResponseDTO> getRecentFiveHotTopics(){
        List<TopicResponseDTO> responseDTOList = new ArrayList<>();

        List<HotTopic> hotTopics = hotTopicRepository.findRecentFiveHotTopics();

        for (HotTopic hotTopic : hotTopics) {
            Topic topic = hotTopic.getTopic();
            responseDTOList.add(new TopicResponseDTO(topic));
        }

        return responseDTOList;
    }
}
