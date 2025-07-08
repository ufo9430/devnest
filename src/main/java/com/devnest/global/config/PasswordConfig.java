package com.devnest.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {
  // 이 Bean은 SecurityConfig, UserDetailsService 등에서 주입받아 사용
  @Bean
  public PasswordEncoder passwordEncoder() {
    // BcryptPasswordEncoder는 스프링에서 기본적으로 권장하는 암호화 방식
    return new BCryptPasswordEncoder();
  }
}
