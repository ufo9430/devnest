package com.devnest.user.service;

import com.devnest.user.domain.User;
import com.devnest.user.dto.request.LoginRequestDto;
import com.devnest.user.dto.response.LoginResponseDto;
import com.devnest.user.dto.response.RegisterResponseDto;
import com.devnest.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberLoginService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public LoginResponseDto login(LoginRequestDto requestDto, HttpSession session) {
    User loginUser = userRepository.findByEmail(requestDto.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

    if(!passwordEncoder.matches(requestDto.getPassword(), loginUser.getPassword())){
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
    }

    session.setAttribute("LOGIN_USER", loginUser.getUserId());

    // 응답 DTO 반환
    return LoginResponseDto.builder()
        .userId(loginUser.getUserId())
        .email(loginUser.getEmail())
        .nickname(loginUser.getNickname())
        .build();
  }
}
