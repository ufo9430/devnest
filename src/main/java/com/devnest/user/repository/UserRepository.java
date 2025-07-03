package com.devnest.user.repository;

import com.devnest.user.domain.Role;
import com.devnest.user.domain.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByNickname(String Nickname);

  // 관리자 기능용 메서드들
  Page<User> findByEmailContainingIgnoreCaseOrNicknameContainingIgnoreCase(
      String email, String nickname, Pageable pageable);

  long countByIsActive(boolean isActive);

  long countByRole(Role role);
}
