package com.devnest.board.service;

import com.devnest.board.domain.Status;
import com.devnest.board.domain.BoardTopic;
import com.devnest.board.dto.TopicResponseDTO;
import com.devnest.board.repository.TopicRepository;
import com.devnest.board.vo.StatisticsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TopicService {
    private final TopicRepository topicRepository;

    @Autowired
    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public List<TopicResponseDTO> getRecentFiveTopics(){
        List<TopicResponseDTO> responseDTOList = new ArrayList<>();

        List<BoardTopic> topics = topicRepository.findRecentFiveTopics();

        for (BoardTopic topic : topics) {
            responseDTOList.add(new TopicResponseDTO(topic));
        }

        return responseDTOList;
    }

    public Page<TopicResponseDTO> getRecentTopics(int page){
        Pageable pageable = PageRequest.of(page, 7);
        List<TopicResponseDTO> responseDTOS = new ArrayList<>();

        Page<BoardTopic> topics = topicRepository.findAllByOrderByCreatedAtDesc(pageable);

        for (BoardTopic topic : topics) {
            responseDTOS.add(new TopicResponseDTO(topic));
        }

        return new PageImpl<>(responseDTOS, pageable, topics.getTotalElements());
    }

    public Page<TopicResponseDTO> getSolvedTopics(int page) {
        Pageable pageable = PageRequest.of(page, 7);
        List<TopicResponseDTO> responseDTOS = new ArrayList<>();

        Page<BoardTopic> topics = topicRepository.findByStatus(Status.RESOLVED, pageable);

        for (BoardTopic topic : topics) {
            responseDTOS.add(new TopicResponseDTO(topic));
        }

        return new PageImpl<>(responseDTOS, pageable, topics.getTotalElements());
    }

    public StatisticsVo getStatistics(){
        long allCount = topicRepository.count();
        long waitingCount = topicRepository.countByStatus(Status.WAITING);
        long solved = topicRepository.countByStatus(Status.RESOLVED);
        return StatisticsVo.builder()
                .allCount(allCount)
                .waitingCount(waitingCount)
                .solvedCount(solved)
                .todayCount(0L)
                .build();
    }

}