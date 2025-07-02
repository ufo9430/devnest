package com.devnest.user.service;

import com.devnest.auth.service.CustomUserDetailsService;
import com.devnest.user.domain.User;
import com.devnest.user.dto.request.LoginRequestDto;
import com.devnest.user.dto.response.LoginResponseDto;
import com.devnest.user.dto.response.RegisterResponseDto;
import com.devnest.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberLoginService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CustomUserDetailsService userDetailsService;

  public LoginResponseDto login(LoginRequestDto requestDto, HttpSession session) {
    User loginUser = userRepository.findByEmail(requestDto.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

    session.setAttribute("LOGIN_USER", loginUser.getUserId());

    UserDetails userDetails = userDetailsService.loadUserByUsername(loginUser.getEmail());  // DB에서 이메일로 사용자 정보 조회, userDetails 객체로 반환.
    if (!userDetails.isEnabled()) {
      throw new IllegalArgumentException("계정이 비활성 상태입니다");
    }
    // UseramePasswordAutheticationToken은 Authentication 인터페이스의 구현체
    Authentication auth = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth); // SecurityContext에 인증 정보 설정.
    // SecurityContext를 HttpSession에 저장
    session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        SecurityContextHolder.getContext());

    // 응답 DTO 반환
    return LoginResponseDto.builder()
        .userId(loginUser.getUserId())
        .email(loginUser.getEmail())
        .nickname(loginUser.getNickname())
        .build();
  }

  public LoginResponseDto findById(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

    return LoginResponseDto.builder()
        .userId(user.getUserId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .role(user.getRole())
        .build();
  }

  public void verifyUser(LoginRequestDto requestDto) {
    User user = userRepository.findByEmail(requestDto.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("이메일이 존재하지 않습니다."));

    if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }
  }
}
