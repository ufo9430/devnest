package com.devnest.board.service;

import com.devnest.board.domain.Status;
import com.devnest.board.domain.Topic;
import com.devnest.board.repository.TopicRepository;
import com.devnest.board.vo.StatisticsVo;
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
        return topicRepository.findAllByOrderByCreatedAtDesc();
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