package com.devnest.board.controller;

import com.devnest.board.domain.Topic;
import com.devnest.board.service.BoardIndexService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class BoardIndexController {
    @Autowired
    private BoardIndexService boardIndexService;


    @RequestMapping("/")
    public String index(Model model, HttpSession session){

        List<Topic> recentTopics = boardIndexService.getRecentFiveTopics();
        model.addAttribute("recent",recentTopics);

        //임시 세션 부여
        session.setAttribute("LOGIN_USER",1L);

        return "index";
    }

}
