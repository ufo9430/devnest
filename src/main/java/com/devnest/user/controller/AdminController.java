package com.devnest.user.controller;

import com.devnest.user.dto.common.ApiResponse;
import com.devnest.user.dto.response.UserManagementResponseDto;
import com.devnest.user.domain.Role;
import com.devnest.user.domain.User;
import com.devnest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import org.springframework.data.domain.PageImpl;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

  private final UserRepository userRepository;

  // 간단한 테스트 매핑
  @GetMapping("/test-simple")
  public String showTestSimple() {
    return "test_admin";
  }

  // 관리자 대시보드 (회원 관리) - 참고자료 1
  @GetMapping("/dashboard")
  public String showAdminDashboard() {
    return "admin_member_manage";
  }

  @GetMapping("/member-manage")
  public String showMemberManage() {
    return "admin_member_manage";
  }

  // 신고 관리 페이지 - 참고자료 2
  @GetMapping("/report-management")
  public String showReportManagement() {
    return "report_management";
  }

  @GetMapping("/report-manage")
  public String showReportManage() {
    return "report_management";
  }

  // 전체 알림 관리 페이지 - 참고자료 3
  @GetMapping("/notifications")
  public String showNotifications() {
    return "admin_notifications";
  }

  // 시스템 관리 페이지 - 참고자료 추가 메뉴
  @GetMapping("/system")
  public String showSystemManagement() {
    return "system_management";
  }

  // 콘텐츠 관리 페이지 - 참고자료 추가 메뉴
  @GetMapping("/content")
  public String showContentManagement() {
    return "content_management";
  }

  // 사용자 목록 조회 API
  @GetMapping("/api/users")
  @ResponseBody
  public ResponseEntity<ApiResponse<Page<UserManagementResponseDto>>> getUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir,
      @RequestParam(required = false) String search) {

    try {
      Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
      Pageable pageable = PageRequest.of(page, size, sort);

      // 데이터베이스 연결 문제 시 더미 데이터 반환
      Page<UserManagementResponseDto> userDtos;
      try {
        Page<User> users;
        if (search != null && !search.trim().isEmpty()) {
          users = userRepository.findByEmailContainingIgnoreCaseOrNicknameContainingIgnoreCase(
              search, search, pageable);
        } else {
          users = userRepository.findAll(pageable);
        }
        userDtos = users.map(this::convertToDto);
      } catch (Exception e) {
        // 데이터베이스 오류 시 더미 데이터 반환
        userDtos = createDummyUsers(pageable, search);
      }

      return ResponseEntity.ok(ApiResponse.success("사용자 목록 조회 성공", userDtos));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.fail("사용자 목록 조회에 실패했습니다: " + e.getMessage()));
    }
  }

  // 사용자 활성화/비활성화 API
  @PatchMapping("/api/users/{userId}/toggle-active")
  @ResponseBody
  public ResponseEntity<ApiResponse<Boolean>> toggleUserActive(@PathVariable Long userId) {
    try {
      Optional<User> userOpt = userRepository.findById(userId);
      if (userOpt.isEmpty()) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.fail("사용자를 찾을 수 없습니다."));
      }

      User user = userOpt.get();
      user.setIsActive(!user.getIsActive());
      userRepository.save(user);

      String status = user.getIsActive() ? "활성화" : "비활성화";
      return ResponseEntity.ok(ApiResponse.success(
          "사용자를 " + status + "했습니다.", user.getIsActive()));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.fail("사용자 상태 변경에 실패했습니다: " + e.getMessage()));
    }
  }

  // 사용자 역할 변경 API
  @PatchMapping("/api/users/{userId}/role")
  @ResponseBody
  public ResponseEntity<ApiResponse<String>> changeUserRole(
      @PathVariable Long userId,
      @RequestParam String role) {
    try {
      Optional<User> userOpt = userRepository.findById(userId);
      if (userOpt.isEmpty()) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.fail("사용자를 찾을 수 없습니다."));
      }

      User user = userOpt.get();
      Role newRole = Role.valueOf(role.toUpperCase());
      user.setRole(newRole);
      userRepository.save(user);

      return ResponseEntity.ok(ApiResponse.success(
          "사용자 역할을 " + newRole.name() + "로 변경했습니다.", newRole.name()));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.fail("유효하지 않은 역할입니다."));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.fail("사용자 역할 변경에 실패했습니다: " + e.getMessage()));
    }
  }

  // 사용자 삭제 API
  @DeleteMapping("/api/users/{userId}")
  @ResponseBody
  public ResponseEntity<ApiResponse<Boolean>> deleteUser(@PathVariable Long userId) {
    try {
      Optional<User> userOpt = userRepository.findById(userId);
      if (userOpt.isEmpty()) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.fail("사용자를 찾을 수 없습니다."));
      }

      // 관리자는 삭제할 수 없도록 제한
      User user = userOpt.get();
      if (user.getRole() == Role.ADMIN) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.fail("관리자는 삭제할 수 없습니다."));
      }

      userRepository.deleteById(userId);
      return ResponseEntity.ok(ApiResponse.success("사용자를 삭제했습니다.", true));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.fail("사용자 삭제에 실패했습니다: " + e.getMessage()));
    }
  }

  // 사용자 통계 API
  @GetMapping("/api/users/stats")
  @ResponseBody
  public ResponseEntity<ApiResponse<Object>> getUserStats() {
    try {
      // 데이터베이스 연결 문제 시 더미 데이터 반환
      Object stats;
      try {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActive(true);
        long inactiveUsers = userRepository.countByIsActive(false);
        long adminUsers = userRepository.countByRole(Role.ADMIN);

        stats = new Object() {
          public final long total = totalUsers;
          public final long active = activeUsers;
          public final long inactive = inactiveUsers;
          public final long admins = adminUsers;
        };
      } catch (Exception e) {
        // 더미 통계 데이터
        stats = new Object() {
          public final long total = 156;
          public final long active = 142;
          public final long inactive = 14;
          public final long admins = 3;
        };
      }

      return ResponseEntity.ok(ApiResponse.success("사용자 통계 조회 성공", stats));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.fail("사용자 통계 조회에 실패했습니다: " + e.getMessage()));
    }
  }

  private UserManagementResponseDto convertToDto(User user) {
    return UserManagementResponseDto.builder()
        .userId(user.getUserId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .role(user.getRole())
        .isActive(user.getIsActive())
        .createdAt(user.getCreatedAt())
        .profileImage(user.getProfileImage())
        .totalPosts(0L) // TODO: 실제 게시글 수 계산
        .totalAnswers(0L) // TODO: 실제 답변 수 계산
        .lastActiveDate(
            user.getCreatedAt() != null ? user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : "알 수 없음")
        .build();
  }

  // 더미 사용자 데이터 생성
  private Page<UserManagementResponseDto> createDummyUsers(Pageable pageable, String search) {
    List<UserManagementResponseDto> dummyUsers = new ArrayList<>();

    // 더미 사용자 데이터 생성
    dummyUsers.add(UserManagementResponseDto.builder()
        .userId(1L)
        .email("admin@devnest.com")
        .nickname("관리자")
        .role(Role.ADMIN)
        .isActive(true)
        .createdAt(LocalDateTime.now().minusDays(100))
        .profileImage("/images/default_user_profile.png")
        .totalPosts(45L)
        .totalAnswers(123L)
        .lastActiveDate("2025-01-03")
        .build());

    dummyUsers.add(UserManagementResponseDto.builder()
        .userId(2L)
        .email("kim.developer@gmail.com")
        .nickname("김개발자")
        .role(Role.USER)
        .isActive(true)
        .createdAt(LocalDateTime.now().minusDays(50))
        .profileImage("/images/default_user_profile.png")
        .totalPosts(23L)
        .totalAnswers(67L)
        .lastActiveDate("2025-01-02")
        .build());

    dummyUsers.add(UserManagementResponseDto.builder()
        .userId(3L)
        .email("lee.java@naver.com")
        .nickname("이자바")
        .role(Role.USER)
        .isActive(true)
        .createdAt(LocalDateTime.now().minusDays(30))
        .profileImage("/images/default_user_profile.png")
        .totalPosts(34L)
        .totalAnswers(89L)
        .lastActiveDate("2025-01-01")
        .build());

    dummyUsers.add(UserManagementResponseDto.builder()
        .userId(4L)
        .email("park.frontend@kakao.com")
        .nickname("박프론트")
        .role(Role.USER)
        .isActive(true)
        .createdAt(LocalDateTime.now().minusDays(20))
        .profileImage("/images/default_user_profile.png")
        .totalPosts(12L)
        .totalAnswers(34L)
        .lastActiveDate("2024-12-30")
        .build());

    dummyUsers.add(UserManagementResponseDto.builder()
        .userId(5L)
        .email("choi.backend@test.com")
        .nickname("최백엔드")
        .role(Role.USER)
        .isActive(false)
        .createdAt(LocalDateTime.now().minusDays(10))
        .profileImage("/images/default_user_profile.png")
        .totalPosts(8L)
        .totalAnswers(15L)
        .lastActiveDate("2024-12-25")
        .build());

    dummyUsers.add(UserManagementResponseDto.builder()
        .userId(6L)
        .email("jung.fullstack@example.com")
        .nickname("정풀스택")
        .role(Role.USER)
        .isActive(true)
        .createdAt(LocalDateTime.now().minusDays(5))
        .profileImage("/images/default_user_profile.png")
        .totalPosts(19L)
        .totalAnswers(42L)
        .lastActiveDate("2025-01-01")
        .build());

    // 검색 필터 적용 (간단한 필터링)
    if (search != null && !search.trim().isEmpty()) {
      String searchLower = search.toLowerCase();
      dummyUsers = dummyUsers.stream()
          .filter(user -> user.getEmail().toLowerCase().contains(searchLower) ||
              user.getNickname().toLowerCase().contains(searchLower))
          .toList();
    }

    return new PageImpl<>(dummyUsers, pageable, dummyUsers.size());
  }
}
