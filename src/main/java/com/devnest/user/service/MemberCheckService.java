package com.devnest.user.service;

import com.devnest.user.domain.User;
import com.devnest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MemberCheckService {

  private final UserRepository userRepository;

  public boolean isEmailDuplicate(String email) {
    return userRepository.existsByEmail(email);
  }

  public boolean isNicknameDuplicate(String nickname) {
    return userRepository.existsByNickname(nickname);
  }

  public void updateNickname(Long userId, String newNickname) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    user.setNickname(newNickname);
    userRepository.save(user);
  }

}

