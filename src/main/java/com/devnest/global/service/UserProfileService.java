package com.devnest.global.service;

import com.devnest.global.vo.UserProfileVo;
import com.devnest.user.domain.User;
import com.devnest.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {
    private final UserRepository repository;

    public UserProfileService(UserRepository repository) {
        this.repository = repository;
    }

    public UserProfileVo getProfile(HttpSession session){
        Long userId = (Long) session.getAttribute("LOGIN_USER");

        User user = repository.findById(userId).orElseThrow(EntityNotFoundException::new);

        return UserProfileVo.builder()
                .user_id(user.getUserId())
                .profile_image(user.getProfileImage())
                .build();
    }
}
