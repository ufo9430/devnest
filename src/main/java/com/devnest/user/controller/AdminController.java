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
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

  private final UserRepository userRepository;

  // 관리자 대시보드 (회원 관리)
  @GetMapping("/member-manage")
  public String showMemberManage() {
    return "admin_member_manage";
  }

  // 신고 관리 페이지 (정적 HTML) - 체크포인트1 복구
  @GetMapping("/report-manage")
  public String showReportManage() {
    return "report_management";
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

      Page<User> users;
      if (search != null && !search.trim().isEmpty()) {
        users = userRepository.findByEmailContainingIgnoreCaseOrNicknameContainingIgnoreCase(
            search, search, pageable);
      } else {
        users = userRepository.findAll(pageable);
      }
      Page<UserManagementResponseDto> userDtos = users.map(this::convertToDto);

      return ResponseEntity.ok(ApiResponse.success("사용자 목록 조회 성공", userDtos));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.fail("사용자 목록 조회에 실패했습니다: " + e.getMessage()));
    }
  }

  // 사용자 통계 API
  @GetMapping("/api/users/stats")
  @ResponseBody
  public ResponseEntity<ApiResponse<Object>> getUserStats() {
    try {
      long totalUsersCount = userRepository.count();
      long activeUsersCount = userRepository.countByIsActive(true);
      long inactiveUsersCount = userRepository.countByIsActive(false);
      long adminUsersCount = userRepository.countByRole(Role.ADMIN);

      Object stats = new Object() {
        public final long totalUsers = totalUsersCount;
        public final long activeUsers = activeUsersCount;
        public final long suspendedUsers = inactiveUsersCount;
        public final long adminUsers = adminUsersCount;
      };

      return ResponseEntity.ok(ApiResponse.success("통계 조회 성공", stats));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.fail("통계 조회에 실패했습니다: " + e.getMessage()));
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

  private UserManagementResponseDto convertToDto(User user) {
    return UserManagementResponseDto.builder()
        .userId(user.getUserId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .role(user.getRole())
        .isActive(user.getIsActive())
        .createdAt(user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
        .build();
  }
}
