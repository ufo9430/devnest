package com.devnest.board.service;

import com.devnest.board.domain.BoardTopic;
import com.devnest.board.dto.TopicResponseDTO;
import com.devnest.board.repository.TopicRepository;
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
    private final TopicRepository topicRepository;

    @Autowired
    public SearchService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public Page<TopicResponseDTO> getSearchResult(String keyword, int page, boolean resolved){
        Pageable pageable = PageRequest.of(page, 7);
        List<TopicResponseDTO> responseDTOS = new ArrayList<>();

        Page<BoardTopic> topics = resolved ? topicRepository.searchByKeyword(pageable, keyword)
                                            : topicRepository.searchSolvedByKeyword(pageable,keyword);

        for (BoardTopic topic : topics) {
            responseDTOS.add(new TopicResponseDTO(topic));
        }

        return new PageImpl<>(responseDTOS, pageable, topics.getTotalElements());
    }
}
