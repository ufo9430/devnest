package com.devnest.global.advice;

import com.devnest.auth.domain.CustomUserDetails;
import com.devnest.global.service.UserProfileService;
import com.devnest.global.vo.UserProfileVo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "com.devnest")
public class UserCheckAdvice {
    private final UserProfileService service;

    @Autowired
    public UserCheckAdvice(UserProfileService service, HttpSession session) {
        this.service = service;
    }

    @ModelAttribute("profile")
    public UserProfileVo checkUserProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 객체가 없을 경우
        // 인증 객체가 없거나 anonymousUser인 경우
        if (authentication == null || !authentication.isAuthenticated()
            || authentication.getPrincipal() instanceof String) {
            return null;
        }

        // 인증된 사용자 정보 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        try{
            return service.getProfile(userId);
        }catch (EntityNotFoundException e){
            return null;
        }
    }
}
