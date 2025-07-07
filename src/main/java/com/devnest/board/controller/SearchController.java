package com.devnest.board.controller;

import com.devnest.board.dto.TopicResponseDTO;
import com.devnest.board.service.SearchService;
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
    public String search(@RequestParam(required = false, defaultValue = "") String keyword,
                         @RequestParam(required = false, defaultValue = "") String tag,
                         @RequestParam(required = false, defaultValue = "false") boolean resolved,
                         @RequestParam(required = false, defaultValue="0") int page,
                         Model model){
        Page<TopicResponseDTO> searchResult;

        if  (keyword.isEmpty() && tag.isEmpty()){
            return "redirect:/topics";
        }else if(keyword.isEmpty()){
            searchResult = searchService.getTagSearchResult(tag,page,resolved);
        }else if(tag.isEmpty()){
            searchResult = searchService.getKeywordSearchResult(keyword, page, resolved);
        }else{
            searchResult = searchService.getKeywordAndTagSearchResult(keyword,tag,page,resolved);
        }

        model.addAttribute("topics", searchResult);
        model.addAttribute("keyword",keyword);
        model.addAttribute("tag",tag);
        model.addAttribute( "resolved",resolved);

        return "search";
    }
}
