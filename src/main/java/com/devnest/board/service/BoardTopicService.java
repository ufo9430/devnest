package com.devnest.board.service;

import com.devnest.board.dto.TopicResponseDTO;
import com.devnest.topic.repository.TopicRepository;
import com.devnest.board.vo.StatisticsVo;
import com.devnest.topic.domain.Topic;
import com.devnest.topic.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BoardTopicService {
    private final TopicRepository topicRepository;
    private final AnswerRepository answerRepository;

    @Autowired
    public BoardTopicService(TopicRepository topicRepository, AnswerRepository answerRepository) {
        this.topicRepository = topicRepository;
        this.answerRepository = answerRepository;
    }

    public List<TopicResponseDTO> getRecentFiveTopics(){
        List<TopicResponseDTO> responseDTOList = new ArrayList<>();

        List<Topic> topics = topicRepository.findRecentFiveTopics();

        for (Topic topic : topics) {
            responseDTOList.add(new TopicResponseDTO(topic));
        }

        return responseDTOList;
    }

    public Page<TopicResponseDTO> getRecentTopics(int page){
        Pageable pageable = PageRequest.of(page, 7);
        List<TopicResponseDTO> responseDTOS = new ArrayList<>();

        Page<Topic> topics = topicRepository.findAllByOrderByCreatedAtDesc(pageable);

        for (Topic topic : topics) {
            responseDTOS.add(new TopicResponseDTO(topic));
        }

        return new PageImpl<>(responseDTOS, pageable, topics.getTotalElements());
    }

    public Page<TopicResponseDTO> getSolvedTopics(int page) {
        Pageable pageable = PageRequest.of(page, 7);
        List<TopicResponseDTO> responseDTOS = new ArrayList<>();

        Page<Topic> topics = topicRepository.findByStatus(Topic.TopicStatus.RESOLVED, pageable);

        for (Topic topic : topics) {
            responseDTOS.add(new TopicResponseDTO(topic));
        }

        return new PageImpl<>(responseDTOS, pageable, topics.getTotalElements());
    }

    public StatisticsVo getStatistics(){
        long allCount = topicRepository.count();
        long waitingCount = topicRepository.countByStatus(Topic.TopicStatus.WAITING);
        long solved = topicRepository.countByStatus(Topic.TopicStatus.RESOLVED);
        long today = answerRepository.countTodayCreated();
        return StatisticsVo.builder()
                .allCount(allCount)
                .waitingCount(waitingCount)
                .solvedCount(solved)
                .todayCount(today)
                .build();
    }

}