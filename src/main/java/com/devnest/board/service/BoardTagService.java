package com.devnest.board.service;

import com.devnest.board.domain.Tag;
import com.devnest.board.repository.BoardTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardTagService {
    private final BoardTagRepository boardTagRepository;

    @Autowired
    public BoardTagService(BoardTagRepository boardTagRepository) {
        this.boardTagRepository = boardTagRepository;
    }

    public List<Tag> findAll(){
        return boardTagRepository.findAll();
    }
}
