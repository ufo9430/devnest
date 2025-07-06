package com.devnest.board.service;

import com.devnest.board.domain.BoardTag;
import com.devnest.board.repository.BoardTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final BoardTagRepository tagRepository;

    @Autowired
    public TagService(BoardTagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<BoardTag> findAll(){
        return tagRepository.findAll();
    }
}
