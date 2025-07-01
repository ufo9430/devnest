package com.devnest.global.config;

import com.devnest.auth.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    http
            .csrf(csrf -> csrf.disable()) // api 테스트용 임시 해제!
            .cors(cors -> cors.disable()) // 🔥 Postman 테스트용 CORS 비활성화
            .securityMatcher("/**") // ✅ 이 라인 추가
            .authorizeHttpRequests(auth -> auth
            .requestMatchers("/member/**", "/email/**","/test/email"// 테스트용 임시!, /email/** <- 비회원도 Api 사용 가능하게 제외해두었습니다!
                    , "/member/reset-password","/member/check-email","/member/check-nickname"
            ).permitAll()
//            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        );

        // 직접 로그인, 로그아웃 API 만들고 HttpSession 처리를 원하기 때문에 제거 .
        // email 로그인, 로그아웃 처리의 경우 직접 구현 방식이 더 유연하게 가능.
//        .formLogin(form -> form
////            .loginPage("/login")  // get 요청으로 받아와야 함. 일단은 Default 사용
//            .loginProcessingUrl("/member/login")  // 로그인 POST 요청
//            .defaultSuccessUrl("/") // 로그인 성공 후 리다이렉트되는 페이지 >> 홈 화면
//        )        .formLogin(form -> form
////            .loginPage("/login")  // get 요청으로 받아와야 함. 일단은 Default 사용
//            .loginProcessingUrl("/member/login")  // 로그인 POST 요청
//            .defaultSuccessUrl("/") // 로그인 성공 후 리다이렉트되는 페이지 >> 홈 화면
//        )

//        .logout(logout -> logout
//            .logoutUrl("/member/logout") // 로그아웃 post 요청
//            .logoutSuccessUrl("/")  // 로그아웃 성공 후 리다이렉트되는 페이지 >> 홈 화면
//            .invalidateHttpSession(true)  // HttpSession 객체 완전히 무효화
//            .deleteCookies("JSESSIONID")  // 브라우저에 저장된 JSESSIONID 쿠키 삭제
//        );

    return http.build();
  }
}
