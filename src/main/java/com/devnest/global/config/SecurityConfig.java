package com.devnest.global.config;

import com.devnest.auth.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity  // Spring Security 필터 체인 활성화
@RequiredArgsConstructor
public class SecurityConfig {
  private final CustomUserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
    http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/member/**").permitAll()
//            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )

        .formLogin(form -> form
//            .loginPage("/login")  // get 요청으로 받아와야 함. 일단은 Default 사용
            .loginProcessingUrl("/member/login")  // 로그인 POST 요청
            .defaultSuccessUrl("/") // 로그인 성공 후 리다이렉트되는 페이지 >> 홈 화면
        )

        .logout(logout -> logout
            .logoutUrl("/member/logout") // 로그아웃 post 요청
            .logoutSuccessUrl("/")  // 로그아웃 성공 후 리다이렉트되는 페이지 >> 홈 화면
            .invalidateHttpSession(true)  // HttpSession 객체 완전히 무효화
            .deleteCookies("JSESSIONID")  // 브라우저에 저장된 JSESSIONID 쿠키 삭제
        );

    return http.build();
  }
}
