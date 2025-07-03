package com.devnest.board.controller;

import com.devnest.board.domain.Topic;
import com.devnest.board.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class AllTopicsController {
    @Autowired
    private TopicService topicService;

    @RequestMapping("/topics")
    public String allTopics(Model model){
        List<Topic> topics = topicService.getRecentTopics();

        model.addAttribute("topics",topics);

        return "all_topics";
    }
}
