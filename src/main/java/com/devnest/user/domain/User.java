package com.devnest.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId;

  @Column(nullable = false, unique = true, length = 50) // unique 설정 여부
  private String nickname;

  @Column(nullable = false, length = 255)
  private String password;

  @Column(name = "profile_image", length = 500)
  private String profileImage;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Role role = Role.USER;

  @CreationTimestamp // 현재 시간으로 자동 설정
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Builder.Default
  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  // 정지 관련 필드들
  @Column(name = "suspended_until")
  private LocalDateTime suspendedUntil;

  @Column(name = "suspend_reason", length = 1000)
  private String suspendReason;

  @Column(name = "suspended_by")
  private Long suspendedBy;

  @Column(name = "suspended_at")
  private LocalDateTime suspendedAt;

  // 정지 관련 메서드들

  /**
   * 현재 정지 상태인지 확인
   */
  public boolean isSuspended() {
    return suspendedUntil != null && LocalDateTime.now().isBefore(suspendedUntil);
  }

  /**
   * 사용자 정지 처리
   */
  public void suspend(int days, String reason, Long adminId) {
    this.suspendedUntil = LocalDateTime.now().plusDays(days);
    this.suspendReason = reason;
    this.suspendedBy = adminId;
    this.suspendedAt = LocalDateTime.now();
  }

  /**
   * 사용자 정지 처리 (특정 날짜까지)
   */
  public void suspend(LocalDateTime until, String reason, Long adminId) {
    this.suspendedUntil = until;
    this.suspendReason = reason;
    this.suspendedBy = adminId;
    this.suspendedAt = LocalDateTime.now();
  }

  /**
   * 정지 해제
   */
  public void unsuspend() {
    this.suspendedUntil = null;
    this.suspendReason = null;
    this.suspendedBy = null;
    this.suspendedAt = null;
  }

  /**
   * 정지 기간 연장
   */
  public void extendSuspension(int additionalDays, String reason, Long adminId) {
    if (isSuspended()) {
      this.suspendedUntil = this.suspendedUntil.plusDays(additionalDays);
    } else {
      this.suspendedUntil = LocalDateTime.now().plusDays(additionalDays);
    }
    this.suspendReason = reason;
    this.suspendedBy = adminId;
    this.suspendedAt = LocalDateTime.now();
  }
}
