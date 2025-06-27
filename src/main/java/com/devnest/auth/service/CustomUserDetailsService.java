package com.devnest.auth.service;

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

    return org.springframework.security.core.userdetails.User
        .withUsername(user.getEmail())  // 인증에 사용할 사용자 이름(여기에선 이메일)
        .password(user.getPassword()) // DB에 저장된 비밀번호(암호화된 상태)
        .authorities(user.getRole())  // Role enum이 GrantedAuthority 구현 (ROLE_USER / ROLE_ADMIN )
        .disabled(!user.getIsActive())  // 계정 활성 상태(false면 로그인 불가)
        .build();
  }
}
