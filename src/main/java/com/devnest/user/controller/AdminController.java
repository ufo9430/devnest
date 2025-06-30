package com.devnest.user.controller;

import com.devnest.user.dto.common.ApiResponse;
import com.devnest.user.dto.response.AdminUserListResponseDto;
import com.devnest.user.dto.response.AdminUserDetailResponseDto;
import com.devnest.user.dto.response.AdminDashboardStatsResponseDto;
import com.devnest.user.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // 관리자만 접근 가능
public class AdminController {

    private final AdminService adminService;

    /**
     * 관리자 대시보드 통계 조회
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<AdminDashboardStatsResponseDto>> getDashboardStats() {
        AdminDashboardStatsResponseDto stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("대시보드 통계 조회 성공", stats));
    }

    /**
     * 회원 목록 조회 (페이징, 검색, 필터링)
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<AdminUserListResponseDto>>> getUserList(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AdminUserListResponseDto> userList = adminService.getUserList(search, status, pageable);
        return ResponseEntity.ok(ApiResponse.success("회원 목록 조회 성공", userList));
    }

    /**
     * 회원 상세 조회
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<AdminUserDetailResponseDto>> getUserDetail(
            @PathVariable Long userId) {
        AdminUserDetailResponseDto userDetail = adminService.getUserDetail(userId);
        return ResponseEntity.ok(ApiResponse.success("회원 상세 조회 성공", userDetail));
    }

    /**
     * 회원 계정 상태 변경 (정지/활성화)
     */
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<Boolean>> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam("isActive") Boolean isActive) {
        Boolean result = adminService.updateUserStatus(userId, isActive);
        String message = isActive ? "계정이 활성화되었습니다." : "계정이 정지되었습니다.";
        return ResponseEntity.ok(ApiResponse.success(message, result));
    }

    /**
     * 회원 권한 변경
     */
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<Boolean>> updateUserRole(
            @PathVariable Long userId,
            @RequestParam("role") String role) {
        Boolean result = adminService.updateUserRole(userId, role);
        return ResponseEntity.ok(ApiResponse.success("회원 권한이 변경되었습니다.", result));
    }
}