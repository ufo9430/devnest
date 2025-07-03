package com.devnest.board.controller;

import com.devnest.auth.domain.CustomUserDetails;
import com.devnest.board.domain.BoardTag;
import com.devnest.board.dto.TopicResponseDTO;
import com.devnest.board.service.TopicService;
import com.devnest.board.service.TagService;
import com.devnest.board.service.HotTopicService;
import com.devnest.board.vo.StatisticsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
        model.addAttribute("recent", recentTopics);

        List<TopicResponseDTO> recentHotTopics = hotTopicService.getRecentFiveHotTopics();
        model.addAttribute("hot", recentHotTopics);

        List<BoardTag> allTags = tagService.findAll();
        model.addAttribute("tags", allTags);

        StatisticsVo statistics = topicService.getStatistics();
        model.addAttribute("statistics", statistics);

        return "index";
    }

    // 일반 사용자용 전체 알림 보기 페이지 (피그마 기준)
    @GetMapping("/notifications")
    public String notifications(Model model) {
        // 사용자 알림 데이터를 모델에 추가 (추후 NotificationService와 연동)
        // model.addAttribute("notifications",
        // notificationService.getUserNotifications(userId));
        return "notifications";
    }
}
