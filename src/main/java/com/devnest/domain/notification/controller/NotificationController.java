package com.devnest.domain.notification.controller;

import com.devnest.user.dto.common.ApiResponse;
import com.devnest.domain.notification.dto.request.SystemNotificationRequestDto;
import com.devnest.domain.notification.dto.response.NotificationListResponseDto;
import com.devnest.domain.notification.dto.response.NotificationStatsResponseDto;
import com.devnest.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 사용자의 알림 목록 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationListResponseDto>>> getNotifications(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            Page<NotificationListResponseDto> notifications;
            if (unreadOnly) {
                notifications = notificationService.getUnreadNotifications(userId, pageable);
            } else {
                notifications = notificationService.getNotifications(userId, pageable);
            }

            return ResponseEntity.ok(ApiResponse.success("알림 목록 조회 성공", notifications));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("알림 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 알림 통계 조회 (읽지 않은 알림 개수 등)
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<NotificationStatsResponseDto>> getNotificationStats(
            @RequestParam Long userId) {
        try {
            NotificationStatsResponseDto stats = notificationService.getNotificationStats(userId);
            return ResponseEntity.ok(ApiResponse.success("알림 통계 조회 성공", stats));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("알림 통계 조회에 실패했습니다."));
        }
    }

    /**
     * 최근 알림 목록 조회 (헤더 드롭다운용)
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<NotificationListResponseDto>>> getRecentNotifications(
            @RequestParam Long userId) {
        try {
            List<NotificationListResponseDto> notifications = notificationService.getRecentNotifications(userId);
            return ResponseEntity.ok(ApiResponse.success("최근 알림 조회 성공", notifications));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("최근 알림 조회에 실패했습니다."));
        }
    }

    /**
     * 특정 알림 읽음 처리
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Boolean>> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {
        try {
            Boolean result = notificationService.markAsRead(notificationId, userId);
            return ResponseEntity.ok(ApiResponse.success("알림을 읽음으로 표시했습니다.", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("알림 읽음 처리에 실패했습니다."));
        }
    }

    /**
     * 모든 알림 읽음 처리
     */
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Integer>> markAllAsRead(
            @RequestParam Long userId) {
        try {
            Integer count = notificationService.markAllAsRead(userId);
            return ResponseEntity.ok(
                    ApiResponse.success(count + "개의 알림을 읽음으로 표시했습니다.", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("전체 알림 읽음 처리에 실패했습니다."));
        }
    }

    /**
     * 선택한 알림들 읽음 처리
     */
    @PatchMapping("/read-selected")
    public ResponseEntity<ApiResponse<Integer>> markSelectedAsRead(
            @RequestBody List<Long> notificationIds,
            @RequestParam Long userId) {
        try {
            Integer count = notificationService.markAsReadByIds(notificationIds, userId);
            return ResponseEntity.ok(
                    ApiResponse.success(count + "개의 알림을 읽음으로 표시했습니다.", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("선택 알림 읽음 처리에 실패했습니다."));
        }
    }

    /**
     * 관리자용 - 시스템 알림 전체 발송
     */
    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Integer>> broadcastSystemNotification(
            @RequestParam String message) {
        try {
            Integer count = notificationService.broadcastSystemNotification(message);
            return ResponseEntity.ok(
                    ApiResponse.success(count + "명에게 시스템 알림을 발송했습니다.", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("시스템 알림 발송에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 관리자용 - 개별 시스템 알림 발송
     */
    @PostMapping("/system")
    // @PreAuthorize("hasRole('ADMIN')") // 테스트용으로 임시 제거
    public ResponseEntity<ApiResponse<Boolean>> createSystemNotification(
            @RequestBody SystemNotificationRequestDto request) {
        try {
            String message = request.getMessage();
            if (request.getTitle() != null && !request.getTitle().isEmpty()) {
                message = request.getTitle() + ": " + message;
            }
            notificationService.createSystemNotification(request.getUserId(), message);
            return ResponseEntity.ok(ApiResponse.success("시스템 알림을 발송했습니다.", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("시스템 알림 발송에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 관리자용 - 오래된 알림 정리
     */
    @DeleteMapping("/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Integer>> cleanupOldNotifications() {
        try {
            Integer count = notificationService.cleanupOldNotifications();
            return ResponseEntity.ok(
                    ApiResponse.success(count + "개의 오래된 알림을 정리했습니다.", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("알림 정리에 실패했습니다: " + e.getMessage()));
        }
    }
}