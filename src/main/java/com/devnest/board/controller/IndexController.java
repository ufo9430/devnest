package com.devnest.board.controller;

import com.devnest.auth.domain.CustomUserDetails;
import com.devnest.board.domain.HotTopic;
import com.devnest.board.domain.Tag;
import com.devnest.board.domain.Topic;
import com.devnest.board.dto.TopicResponseDTO;
import com.devnest.board.service.TopicService;
import com.devnest.board.service.TagService;
import com.devnest.board.service.HotTopicService;
import com.devnest.board.vo.StatisticsVo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
    public String index(Model model, Authentication authentication) {

        // 로그인한 사용자 정보 확인
        if (authentication != null && authentication.isAuthenticated()
            && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

            Long userId = userDetails.getUserId();
            String email = userDetails.getEmail();
            String nickname = userDetails.getNickname();

            // 콘솔 확인용
            System.out.println("로그인 유저 정보:");
            System.out.println(" - ID: " + userId);
            System.out.println(" - Email: " + email);
            System.out.println(" - Nickname: " + nickname);

            if (userDetails.isAdmin()) {
                return "redirect:/admin";
            }

            model.addAttribute("profile", userDetails);
        } else {
            System.out.println("인증되지 않은 사용자입니다.");
        }

        List<TopicResponseDTO> recentTopics = topicService.getRecentFiveTopics();
        model.addAttribute("recent",recentTopics);

        List<TopicResponseDTO> recentHotTopics = hotTopicService.getRecentFiveHotTopics();
        model.addAttribute("hot",recentHotTopics);

        List<Tag> allTags = tagService.findAll();
        model.addAttribute("tags",allTags);

        StatisticsVo statistics = topicService.getStatistics();
        model.addAttribute("statistics",statistics);

        return "index";
    }

}
