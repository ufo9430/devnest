package com.devnest.board.controller;

import com.devnest.board.dto.TopicResponseDTO;
import com.devnest.board.service.BoardTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AllTopicsController {
    @Autowired
    private BoardTopicService boardTopicService;

    @RequestMapping("/topics")
    public String allTopics(@RequestParam(required = false, defaultValue = "false") boolean resolved,
                            @RequestParam(required = false, defaultValue="0") int page,
                            Model model){
        Page<TopicResponseDTO> topics = resolved ? boardTopicService.getSolvedTopics(page) : boardTopicService.getRecentTopics(page);

        model.addAttribute("resolved",resolved);
        model.addAttribute("topics",topics);

        return "all_topics";
    }
}
