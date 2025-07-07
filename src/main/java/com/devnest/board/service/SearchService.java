package com.devnest.board.service;

import com.devnest.board.domain.BoardTopic;
import com.devnest.board.dto.TopicResponseDTO;
import com.devnest.board.repository.BoardTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    private final BoardTopicRepository topicRepository;

    @Autowired
    public SearchService(BoardTopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public Page<TopicResponseDTO> getKeywordSearchResult(String keyword, int page, boolean resolved) {
        Pageable pageable = PageRequest.of(page, 7);
        List<TopicResponseDTO> responseDTOS = new ArrayList<>();

        Page<BoardTopic> topics = resolved ? topicRepository.searchSolvedByKeyword(pageable, keyword)
                : topicRepository.searchByKeyword(pageable, keyword);

        for (BoardTopic topic : topics) {
            responseDTOS.add(new TopicResponseDTO(topic));
        }

        return new PageImpl<>(responseDTOS, pageable, topics.getTotalElements());
    }

    public Page<TopicResponseDTO> getTagSearchResult(String tag, int page, boolean resolved) {
        Pageable pageable = PageRequest.of(page, 7);
        List<TopicResponseDTO> responseDTOS = new ArrayList<>();

        Page<BoardTopic> topics = resolved ? topicRepository.searchSolvedByTag(pageable, tag)
                : topicRepository.searchByTag(pageable, tag);

        for (BoardTopic topic : topics) {
            responseDTOS.add(new TopicResponseDTO(topic));
        }

        return new PageImpl<>(responseDTOS, pageable, topics.getTotalElements());
    }

    public Page<TopicResponseDTO> getKeywordAndTagSearchResult(String keyword, String tag, int page, boolean resolved) {
        Pageable pageable = PageRequest.of(page, 7);
        List<TopicResponseDTO> responseDTOS = new ArrayList<>();

        Page<BoardTopic> topics = resolved ? topicRepository.searchSolvedByKeywordAndTag(pageable, keyword, tag)
                : topicRepository.searchByKeywordAndTag(pageable, keyword, tag);

        for (BoardTopic topic : topics) {
            responseDTOS.add(new TopicResponseDTO(topic));
        }

        return new PageImpl<>(responseDTOS, pageable, topics.getTotalElements());
    }

}
