package com.devnest.admin.controller;

import com.devnest.admin.dto.request.UserSuspendRequestDto;
import com.devnest.admin.dto.request.UserUnsuspendRequestDto;
import com.devnest.admin.dto.response.UserSuspendResponseDto;
import com.devnest.user.domain.User;
import com.devnest.user.dto.common.ApiResponse;
import com.devnest.user.repository.UserRepository;
import com.devnest.notice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/api")
// @PreAuthorize("hasRole('ADMIN')") // 임시 주석 처리
public class AdminApiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> users;

        if (search == null || search.trim().isEmpty()) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findByEmailContainingIgnoreCaseOrNicknameContainingIgnoreCase(
                    search.trim(), search.trim(), pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "사용자 목록 조회 성공");
        response.put("data", users);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActive(true);
        long inactiveUsers = userRepository.countByIsActive(false);
        long adminUsers = userRepository.countByRole(com.devnest.user.domain.Role.ADMIN);
        long suspendedUsers = userRepository.countSuspendedUsers(LocalDateTime.now());

        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("inactiveUsers", inactiveUsers);
        stats.put("adminUsers", adminUsers);
        stats.put("suspendedUsers", suspendedUsers);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "사용자 통계 조회 성공");
        response.put("data", stats);

        return ResponseEntity.ok(response);
    }

    // 사용자 정지 관련 API들 (알림 연동)

    /**
     * 사용자 정지 (알림 연동)
     */
    @PostMapping("/users/{userId}/suspend")
    public ResponseEntity<ApiResponse<UserSuspendResponseDto>> suspendUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserSuspendRequestDto requestDto) {

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

            if (user.isSuspended()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.fail("이미 정지된 사용자입니다."));
            }

            user.suspend(requestDto.getDays(), requestDto.getReason(), requestDto.getAdminId());
            userRepository.save(user);

            // 정지 알림 생성
            try {
                notificationService.createSuspendNotification(
                        userId,
                        requestDto.getReason(),
                        user.getSuspendedUntil());
                log.info("사용자 정지 알림 발송 완료 - 사용자 ID: {}, 정지 기간: {}일",
                        userId, requestDto.getDays());
            } catch (Exception e) {
                log.error("사용자 정지 알림 발송 중 오류 발생: {}", e.getMessage());
            }

            UserSuspendResponseDto responseDto = UserSuspendResponseDto.fromEntity(user);
            return ResponseEntity.ok(ApiResponse.success("사용자 정지가 완료되었습니다.", responseDto));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("사용자 정지 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 사용자 정지 해제 (알림 연동)
     */
    @PostMapping("/users/{userId}/unsuspend")
    public ResponseEntity<ApiResponse<UserSuspendResponseDto>> unsuspendUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUnsuspendRequestDto requestDto) {

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

            if (!user.isSuspended()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.fail("정지되지 않은 사용자입니다."));
            }

            // 정지 해제 전 정보 저장
            String originalSuspendReason = user.getSuspendReason();
            LocalDateTime originalSuspendedUntil = user.getSuspendedUntil();

            user.unsuspend();
            userRepository.save(user);

            // 정지 해제 알림 생성
            try {
                String unsuspendMessage = String.format(
                        "계정 정지가 해제되었습니다.\n원래 정지 사유: %s\n원래 해제 예정일: %s\n해제 사유: %s\n\n이제 정상적으로 서비스를 이용하실 수 있습니다.",
                        originalSuspendReason != null ? originalSuspendReason : "정보 없음",
                        originalSuspendedUntil != null ? originalSuspendedUntil.toString() : "정보 없음",
                        requestDto.getReason() != null ? requestDto.getReason() : "관리자 조치");

                notificationService.createSystemNotification(userId, unsuspendMessage);
                log.info("사용자 정지 해제 알림 발송 완료 - 사용자 ID: {}", userId);
            } catch (Exception e) {
                log.error("사용자 정지 해제 알림 발송 중 오류 발생: {}", e.getMessage());
            }

            UserSuspendResponseDto responseDto = UserSuspendResponseDto.fromEntity(user);
            return ResponseEntity.ok(ApiResponse.success("사용자 정지가 해제되었습니다.", responseDto));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("사용자 정지 해제 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 정지 기간 연장 (알림 연동)
     */
    @PostMapping("/users/{userId}/extend-suspension")
    public ResponseEntity<ApiResponse<UserSuspendResponseDto>> extendSuspension(
            @PathVariable Long userId,
            @Valid @RequestBody UserSuspendRequestDto requestDto) {

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

            // 연장 전 정보 저장
            LocalDateTime originalSuspendedUntil = user.getSuspendedUntil();

            user.extendSuspension(requestDto.getDays(), requestDto.getReason(), requestDto.getAdminId());
            userRepository.save(user);

            // 정지 기간 연장 알림 생성
            try {
                String extendMessage = String.format(
                        "계정 정지 기간이 연장되었습니다.\n연장 사유: %s\n추가 정지 기간: %d일\n기존 해제 예정일: %s\n새로운 해제 예정일: %s",
                        requestDto.getReason(),
                        requestDto.getDays(),
                        originalSuspendedUntil != null ? originalSuspendedUntil.toString() : "정보 없음",
                        user.getSuspendedUntil().toString());

                notificationService.createSystemNotification(userId, extendMessage);
                log.info("사용자 정지 기간 연장 알림 발송 완료 - 사용자 ID: {}, 추가 기간: {}일",
                        userId, requestDto.getDays());
            } catch (Exception e) {
                log.error("사용자 정지 기간 연장 알림 발송 중 오류 발생: {}", e.getMessage());
            }

            UserSuspendResponseDto responseDto = UserSuspendResponseDto.fromEntity(user);
            return ResponseEntity.ok(ApiResponse.success("정지 기간이 연장되었습니다.", responseDto));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("정지 기간 연장 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 정지된 사용자 목록 조회
     */
    @GetMapping("/users/suspended")
    public ResponseEntity<ApiResponse<Page<UserSuspendResponseDto>>> getSuspendedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("suspendedAt").descending());
            Page<User> suspendedUsers = userRepository.findSuspendedUsers(LocalDateTime.now(), pageable);

            Page<UserSuspendResponseDto> responseDto = suspendedUsers.map(UserSuspendResponseDto::fromEntity);
            return ResponseEntity.ok(ApiResponse.success("정지된 사용자 목록 조회 성공", responseDto));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("정지된 사용자 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 정지 이력이 있는 사용자 목록 조회
     */
    @GetMapping("/users/suspend-history")
    public ResponseEntity<ApiResponse<Page<UserSuspendResponseDto>>> getUsersWithSuspendHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("suspendedAt").descending());
            Page<User> usersWithHistory = userRepository.findUsersWithSuspendHistory(pageable);

            Page<UserSuspendResponseDto> responseDto = usersWithHistory.map(UserSuspendResponseDto::fromEntity);
            return ResponseEntity.ok(ApiResponse.success("정지 이력 조회 성공", responseDto));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("정지 이력 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 사용자 정지 상태 조회
     */
    @GetMapping("/users/{userId}/suspend-status")
    public ResponseEntity<ApiResponse<UserSuspendResponseDto>> getUserSuspendStatus(
            @PathVariable Long userId) {

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

            UserSuspendResponseDto responseDto = UserSuspendResponseDto.fromEntity(user);
            return ResponseEntity.ok(ApiResponse.success("사용자 정지 상태 조회 성공", responseDto));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("사용자 정지 상태 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}