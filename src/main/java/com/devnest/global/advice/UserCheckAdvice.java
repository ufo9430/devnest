package com.devnest.global.advice;

import com.devnest.global.service.UserProfileService;
import com.devnest.global.vo.UserProfileVo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "com.devnest")
public class UserCheckAdvice {
    private final UserProfileService service;
    private final HttpSession session;

    @Autowired
    public UserCheckAdvice(UserProfileService service, HttpSession session) {
        this.service = service;
        this.session = session;
    }

    @ModelAttribute("profile")
    public UserProfileVo checkUserProfile(){
        Long user_id = (Long) session.getAttribute("LOGIN_USER");

        if(user_id == null) return null;

        try{
            return service.getProfile(session);
        }catch (EntityNotFoundException e){
            return null;
        }
    }
}
