package com.devnest.user.repository;

import com.devnest.user.domain.Role;
import com.devnest.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByNickname(String Nickname);

  // 관리자 기능용 메서드들
  Page<User> findByEmailContainingIgnoreCaseOrNicknameContainingIgnoreCase(
      String email, String nickname, Pageable pageable);

  long countByIsActive(boolean isActive);

  long countByRole(Role role);

  // 정지된 사용자 관리 메서드들

  /**
   * 현재 정지된 사용자 목록 조회
   */
  @Query("SELECT u FROM User u WHERE u.suspendedUntil IS NOT NULL AND u.suspendedUntil > :currentTime")
  Page<User> findSuspendedUsers(@Param("currentTime") LocalDateTime currentTime, Pageable pageable);

  /**
   * 정지된 사용자 수 조회
   */
  @Query("SELECT COUNT(u) FROM User u WHERE u.suspendedUntil IS NOT NULL AND u.suspendedUntil > :currentTime")
  long countSuspendedUsers(@Param("currentTime") LocalDateTime currentTime);

  /**
   * 정지 만료된 사용자 목록 조회 (배치 작업용)
   */
  @Query("SELECT u FROM User u WHERE u.suspendedUntil IS NOT NULL AND u.suspendedUntil <= :currentTime")
  List<User> findExpiredSuspendedUsers(@Param("currentTime") LocalDateTime currentTime);

  /**
   * 특정 관리자가 정지시킨 사용자 목록 조회
   */
  @Query("SELECT u FROM User u WHERE u.suspendedBy = :adminId AND u.suspendedUntil IS NOT NULL")
  Page<User> findUsersSuspendedByAdmin(@Param("adminId") Long adminId, Pageable pageable);

  /**
   * 정지 이력이 있는 사용자 조회 (정지 해제된 사용자 포함)
   */
  @Query("SELECT u FROM User u WHERE u.suspendedAt IS NOT NULL")
  Page<User> findUsersWithSuspendHistory(Pageable pageable);

  // 역할별 사용자 조회
  List<User> findByRole(Role role);
}
