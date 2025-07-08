package com.devnest.board.service;

import com.devnest.board.domain.HotTopic;
import com.devnest.board.dto.TopicResponseDTO;
import com.devnest.board.repository.HotTopicRepository;
import com.devnest.topic.domain.Topic;
import com.devnest.topic.repository.TopicRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HotTopicService {

    private final HotTopicRepository hotTopicRepository;
    private final TopicRepository topicRepository;

    @Autowired
    public HotTopicService(HotTopicRepository hotTopicRepository, TopicRepository topicRepository) {
        this.hotTopicRepository = hotTopicRepository;
        this.topicRepository = topicRepository;
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
    // 인기글 확인
    public boolean isHotTopic(Long topicId){
        try{
            Topic topic = topicRepository.findById(topicId).orElseThrow(EntityNotFoundException::new);

            if(hotTopicRepository.existsByTopicId(topicId) == 1L){
                return false;
            }

            return topic.getViewCount() >= 100;

        }catch (EntityNotFoundException e){
            return false;
        }
    }

    public void createHotTopicByTopicId(Long topicId){
        Topic topic = topicRepository.findById(topicId).orElseThrow(EntityNotFoundException::new);

        HotTopic hotTopic = HotTopic.builder()
                .topic(topic)
                .snapshotTime(LocalDateTime.now())
                .build();

        hotTopicRepository.save(hotTopic);
    }
}
