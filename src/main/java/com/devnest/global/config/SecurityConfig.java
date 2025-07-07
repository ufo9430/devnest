package com.devnest.global.config;

import com.devnest.auth.service.CustomUserDetailsService;
import com.devnest.global.handler.CustomAccessDeniedHandler;
import com.devnest.global.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity  // Spring Security 필터 체인 활성화
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomUserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> {
        })  // csrf 활성화
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/member/**", "/email/**", "/test/email", "/topics", "/search")
            .permitAll()
            .requestMatchers(HttpMethod.GET, "/topics/**").authenticated()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        ).exceptionHandling(ex ->
            ex.accessDeniedHandler(new CustomAccessDeniedHandler())
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
    return http.build();
  }
}
