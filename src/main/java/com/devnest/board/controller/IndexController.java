package com.devnest.board.controller;

import com.devnest.board.domain.HotTopic;
import com.devnest.board.domain.Tag;
import com.devnest.board.domain.Topic;
import com.devnest.board.service.TopicService;
import com.devnest.board.service.TagService;
import com.devnest.board.service.HotTopicService;
import com.devnest.board.vo.StatisticsVo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private TopicService topicService;
    @Autowired
    private HotTopicService hotTopicService;
    @Autowired
    private TagService tagService;


    @RequestMapping("/")
    public String index(Model model, HttpSession session){

        List<Topic> recentTopics = topicService.getRecentFiveTopics();
        model.addAttribute("recent",recentTopics);

        List<HotTopic> recentHotTopics = hotTopicService.getRecentFiveHotTopics();
        model.addAttribute("hot",recentHotTopics);

        List<Tag> allTags = tagService.findAll();
        model.addAttribute("tags",allTags);

        StatisticsVo statistics = topicService.getStatistics();
        model.addAttribute("statistics",statistics);

        //임시 세션 부여
        session.setAttribute("LOGIN_USER",1L);

        return "index";
    }

}
