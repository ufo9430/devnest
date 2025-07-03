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
}
