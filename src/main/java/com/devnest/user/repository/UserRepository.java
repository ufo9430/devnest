package com.devnest.user.repository;

import com.devnest.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  // 관리자 기능용 쿼리 메서드들
  long countByIsActive(boolean isActive);

  long countByCreatedAtAfter(LocalDateTime dateTime);

  Page<User> findByIsActive(boolean isActive, Pageable pageable);

  Page<User> findByNicknameContainingOrEmailContaining(String nickname, String email, Pageable pageable);

  Page<User> findByNicknameContainingOrEmailContainingAndIsActive(String nickname, String email, boolean isActive,
      Pageable pageable);

  // 알림 시스템용 메서드
  List<User> findByIsActiveTrue();
}
