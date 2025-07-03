package com.devnest.board.controller;

import com.devnest.board.dto.TopicResponseDTO;
import com.devnest.board.service.SearchService;
import com.devnest.board.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping
    public String search(@RequestParam String keyword,
                         @RequestParam(required = false, defaultValue = "false") boolean resolved,
                         @RequestParam(required = false, defaultValue="0") int page,
                         Model model){
        Page<TopicResponseDTO> searchResult = searchService.getSearchResult(keyword, page, resolved);
        model.addAttribute("topics", searchResult);

        return "search";
    }
}
