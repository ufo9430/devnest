package com.devnest.user.service;

import com.devnest.user.domain.Role;
import com.devnest.user.domain.User;
import com.devnest.user.dto.common.EmailRequestDto;
import com.devnest.user.dto.request.RegisterRequestDto;
import com.devnest.user.dto.response.RegisterResponseDto;
import com.devnest.user.repository.UserRepository;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberRegisterService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public RegisterResponseDto register(RegisterRequestDto requestDto) {
    // TODO: 나중에 이메일 중복 체크 부분 필요!!

    // TODO: 닉네임 중복 체크 부분도 필요!!

    // 비밀번호 암호화
    String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

    // 사용자 객체 생성
    User user = User.builder()
        .email(requestDto.getEmail())
        .password(encodedPassword)  // 암호화된 비밀번호 사용
        .nickname(requestDto.getNickname())
        .profileImage("/images/default_user_profile.png")
        .role(Role.USER)
        .isActive(true)
        .build();

    // 사용자 등록
    User savedUser = userRepository.save(user);

    // 응답 DTO 반환
    return RegisterResponseDto.builder()
        .userId(savedUser.getUserId())
        .email(savedUser.getEmail())
        .nickname(savedUser.getNickname())
        .build();
  }
}
