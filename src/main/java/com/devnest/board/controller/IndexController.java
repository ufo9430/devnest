package com.devnest.board.controller;

import com.devnest.board.domain.BoardTag;
import com.devnest.board.dto.TopicResponseDTO;
import com.devnest.board.service.TopicService;
import com.devnest.board.service.TagService;
import com.devnest.board.service.HotTopicService;
import com.devnest.board.vo.StatisticsVo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String index(Model model, HttpSession session) {

        List<TopicResponseDTO> recentTopics = topicService.getRecentFiveTopics();
        model.addAttribute("recent", recentTopics);

        List<TopicResponseDTO> recentHotTopics = hotTopicService.getRecentFiveHotTopics();
        model.addAttribute("hot", recentHotTopics);

        List<BoardTag> allTags = tagService.findAll();
        model.addAttribute("tags", allTags);

        StatisticsVo statistics = topicService.getStatistics();
        model.addAttribute("statistics", statistics);

        // 임시 세션 부여
        session.setAttribute("LOGIN_USER", 1L);

        return "index";
    }

    // 일반 사용자용 전체 알림 보기 페이지 (피그마 기준)
    @GetMapping("/notifications")
    public String notifications(Model model, HttpSession session) {
        // 사용자 알림 데이터를 모델에 추가 (추후 NotificationService와 연동)
        // model.addAttribute("notifications",
        // notificationService.getUserNotifications(userId));
        return "notifications";
    }

}
