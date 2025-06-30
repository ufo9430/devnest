package com.devnest.user.service;

import com.devnest.user.domain.Role;
import com.devnest.user.domain.User;
import com.devnest.user.dto.response.AdminDashboardStatsResponseDto;
import com.devnest.user.dto.response.AdminUserDetailResponseDto;
import com.devnest.user.dto.response.AdminUserListResponseDto;
import com.devnest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;

    /**
     * 대시보드 통계 조회
     */
    public AdminDashboardStatsResponseDto getDashboardStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActive(true);
        long suspendedUsers = userRepository.countByIsActive(false);
        long todayNewUsers = userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(1));

        return AdminDashboardStatsResponseDto.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .suspendedUsers(suspendedUsers)
                .todayNewUsers(todayNewUsers)
                .build();
    }

    /**
     * 회원 목록 조회 (페이징, 검색, 필터링)
     */
    public Page<AdminUserListResponseDto> getUserList(String search, String status, Pageable pageable) {
        Page<User> users;

        if (search != null && !search.trim().isEmpty()) {
            // 검색어가 있는 경우
            if ("active".equals(status)) {
                users = userRepository.findByNicknameContainingOrEmailContainingAndIsActive(
                        search, search, true, pageable);
            } else if ("suspended".equals(status)) {
                users = userRepository.findByNicknameContainingOrEmailContainingAndIsActive(
                        search, search, false, pageable);
            } else {
                users = userRepository.findByNicknameContainingOrEmailContaining(
                        search, search, pageable);
            }
        } else {
            // 검색어가 없는 경우
            if ("active".equals(status)) {
                users = userRepository.findByIsActive(true, pageable);
            } else if ("suspended".equals(status)) {
                users = userRepository.findByIsActive(false, pageable);
            } else {
                users = userRepository.findAll(pageable);
            }
        }

        return users.map(AdminUserListResponseDto::from);
    }

    /**
     * 회원 상세 조회
     */
    public AdminUserDetailResponseDto getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // TODO: 게시글 수, 댓글 수 등은 추후 구현
        return AdminUserDetailResponseDto.from(user, 0, 0);
    }

    /**
     * 회원 계정 상태 변경
     */
    @Transactional
    public Boolean updateUserStatus(Long userId, Boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        user.setIsActive(isActive);
        userRepository.save(user);

        return true;
    }

    /**
     * 회원 권한 변경
     */
    @Transactional
    public Boolean updateUserRole(Long userId, String roleStr) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Role role = Role.valueOf(roleStr.toUpperCase());
        user.setRole(role);
        userRepository.save(user);

        return true;
    }
}