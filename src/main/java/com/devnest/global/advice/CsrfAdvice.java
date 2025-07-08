package com.devnest.global.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// 모든 컨트롤러에 _csrf 자동 주입
@ControllerAdvice
public class CsrfAdvice {
  @ModelAttribute("_csrf")
  public CsrfToken csrfToken(HttpServletRequest request) {
    return (CsrfToken) request.getAttribute("_csrf");
  }
}
