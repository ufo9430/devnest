package com.devnest.notice.controller;

import com.devnest.user.dto.common.ApiResponse;
import com.devnest.notice.dto.request.SystemNotificationRequestDto;
import com.devnest.notice.dto.response.NotificationListResponseDto;
import com.devnest.notice.dto.response.NotificationStatsResponseDto;
import com.devnest.notice.service.NotificationService;
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
                    .body(ApiResponse.fail("알림 정리에 실패했습니다."));
        }
    }

    /**
     * 고도화된 알림 목록 조회 (우선순위 + 시간순 정렬)
     */
    @GetMapping("/advanced")
    public ResponseEntity<ApiResponse<Page<NotificationListResponseDto>>> getNotificationsAdvanced(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            Page<NotificationListResponseDto> notifications;
            if (unreadOnly) {
                notifications = notificationService.getUnreadNotificationsAdvanced(userId, pageable);
            } else {
                notifications = notificationService.getNotificationsAdvanced(userId, pageable);
            }

            return ResponseEntity.ok(ApiResponse.success("고도화된 알림 목록 조회 성공", notifications));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("고도화된 알림 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 복합 필터를 사용한 알림 조회
     */
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<NotificationListResponseDto>>> getNotificationsWithFilters(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) String keyword) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            com.devnest.notice.entity.NotificationType notificationType = null;
            if (type != null && !type.isEmpty()) {
                notificationType = com.devnest.notice.entity.NotificationType.valueOf(type.toUpperCase());
            }

            com.devnest.notice.entity.NotificationPriority notificationPriority = null;
            if (priority != null && !priority.isEmpty()) {
                notificationPriority = com.devnest.notice.entity.NotificationPriority.valueOf(priority.toUpperCase());
            }

            Page<NotificationListResponseDto> notifications = notificationService.getNotificationsWithFilters(
                    userId, notificationType, notificationPriority, isRead, keyword, pageable);

            return ResponseEntity.ok(ApiResponse.success("필터링된 알림 목록 조회 성공", notifications));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("필터링된 알림 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 알림 검색
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<NotificationListResponseDto>>> searchNotifications(
            @RequestParam Long userId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<NotificationListResponseDto> notifications = notificationService.searchNotifications(
                    userId, keyword, pageable);

            return ResponseEntity.ok(ApiResponse.success("알림 검색 성공", notifications));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("알림 검색에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 타입별 알림 조회
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<Page<NotificationListResponseDto>>> getNotificationsByType(
            @PathVariable String type,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            com.devnest.notice.entity.NotificationType notificationType = com.devnest.notice.entity.NotificationType
                    .valueOf(type.toUpperCase());

            Page<NotificationListResponseDto> notifications = notificationService.getNotificationsByType(
                    userId, notificationType, pageable);

            return ResponseEntity.ok(ApiResponse.success("타입별 알림 조회 성공", notifications));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("타입별 알림 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 우선순위별 알림 조회
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<ApiResponse<Page<NotificationListResponseDto>>> getNotificationsByPriority(
            @PathVariable String priority,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            com.devnest.notice.entity.NotificationPriority notificationPriority = com.devnest.notice.entity.NotificationPriority
                    .valueOf(priority.toUpperCase());

            Page<NotificationListResponseDto> notifications = notificationService.getNotificationsByPriority(
                    userId, notificationPriority, pageable);

            return ResponseEntity.ok(ApiResponse.success("우선순위별 알림 조회 성공", notifications));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("우선순위별 알림 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 긴급 알림 조회
     */
    @GetMapping("/urgent")
    public ResponseEntity<ApiResponse<List<NotificationListResponseDto>>> getUrgentNotifications(
            @RequestParam Long userId) {
        try {
            List<NotificationListResponseDto> notifications = notificationService.getUrgentNotifications(userId);
            return ResponseEntity.ok(ApiResponse.success("긴급 알림 조회 성공", notifications));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("긴급 알림 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 알림 읽음/읽지 않음 상태 토글
     */
    @PatchMapping("/{notificationId}/toggle")
    public ResponseEntity<ApiResponse<Boolean>> toggleReadStatus(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {
        try {
            Boolean isRead = notificationService.toggleReadStatus(notificationId, userId);
            String message = isRead ? "알림을 읽음으로 표시했습니다." : "알림을 읽지 않음으로 표시했습니다.";
            return ResponseEntity.ok(ApiResponse.success(message, isRead));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("알림 상태 토글에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 선택한 알림들 소프트 삭제
     */
    @DeleteMapping("/soft-delete")
    public ResponseEntity<ApiResponse<Integer>> softDeleteNotifications(
            @RequestBody List<Long> notificationIds,
            @RequestParam Long userId) {
        try {
            Integer count = notificationService.softDeleteNotifications(notificationIds, userId);
            return ResponseEntity.ok(
                    ApiResponse.success(count + "개의 알림을 삭제했습니다.", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("알림 삭제에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 모든 삭제 가능한 알림을 소프트 삭제
     */
    @DeleteMapping("/soft-delete-all")
    public ResponseEntity<ApiResponse<Integer>> softDeleteAllNotifications(
            @RequestParam Long userId) {
        try {
            Integer count = notificationService.softDeleteAllNotifications(userId);
            return ResponseEntity.ok(
                    ApiResponse.success(count + "개의 모든 알림을 삭제했습니다.", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("전체 알림 삭제에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 읽은 알림들만 소프트 삭제
     */
    @DeleteMapping("/soft-delete-read")
    public ResponseEntity<ApiResponse<Integer>> softDeleteReadNotifications(
            @RequestParam Long userId) {
        try {
            Integer count = notificationService.softDeleteReadNotifications(userId);
            return ResponseEntity.ok(
                    ApiResponse.success(count + "개의 읽은 알림을 삭제했습니다.", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("읽은 알림 삭제에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 삭제 가능한 알림 목록 조회
     */
    @GetMapping("/deletable")
    public ResponseEntity<ApiResponse<Page<NotificationListResponseDto>>> getDeletableNotifications(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<NotificationListResponseDto> notifications = notificationService.getDeletableNotifications(userId,
                    pageable);
            return ResponseEntity.ok(ApiResponse.success("삭제 가능한 알림 목록 조회 성공", notifications));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("삭제 가능한 알림 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 고도화된 알림 통계 조회
     */
    @GetMapping("/stats/advanced")
    public ResponseEntity<ApiResponse<NotificationStatsResponseDto>> getNotificationStatsAdvanced(
            @RequestParam Long userId) {
        try {
            NotificationStatsResponseDto stats = notificationService.getNotificationStatsAdvanced(userId);
            return ResponseEntity.ok(ApiResponse.success("고도화된 알림 통계 조회 성공", stats));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("고도화된 알림 통계 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 관리자용 - 자동 삭제 시간이 지난 알림들 정리
     */
    @DeleteMapping("/cleanup-expired")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Integer>> cleanupExpiredNotifications() {
        try {
            Integer count = notificationService.cleanupExpiredNotifications();
            return ResponseEntity.ok(
                    ApiResponse.success(count + "개의 만료된 알림을 정리했습니다.", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("만료된 알림 정리에 실패했습니다: " + e.getMessage()));
        }
    }
}