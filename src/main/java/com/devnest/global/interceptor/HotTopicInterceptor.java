package com.devnest.global.interceptor;

import com.devnest.board.service.HotTopicService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Component
public class HotTopicInterceptor implements HandlerInterceptor {
    private final HotTopicService hotTopicService;

    @Autowired
    public HotTopicInterceptor(HotTopicService hotTopicService) {
        this.hotTopicService = hotTopicService;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>)
                request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables != null && pathVariables.containsKey("topicId")) {
            Long topicId = Long.parseLong(pathVariables.get("topicId"));

            if(hotTopicService.isHotTopic(topicId)){
                hotTopicService.createHotTopicByTopicId(topicId);
            }
        }
    }
}
