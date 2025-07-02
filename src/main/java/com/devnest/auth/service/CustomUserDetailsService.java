package com.devnest.auth.service;

import com.devnest.auth.domain.CustomUserDetails;
import com.devnest.user.domain.User;
import com.devnest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  // DB에서 사용자 정보를 가져오기 위한 JPA Repository
  private final UserRepository userRepository;

  // 로그인 시 이메일로 사용자 정보를 불러오고, Spring Security의 UserDetails로 변환
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    User user = userRepository.findByEmail(email)
        .orElseThrow(() ->
            new UsernameNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

    return new CustomUserDetails(user);
  }
}
