package com.devnest.report.service;

import com.devnest.report.dto.request.ReportCreateRequestDto;
import com.devnest.report.dto.request.ReportProcessRequestDto;
import com.devnest.report.dto.response.ReportListResponseDto;
import com.devnest.report.dto.response.ReportStatsResponseDto;
import com.devnest.report.entity.Report;
import com.devnest.report.entity.ReportStatus;
import com.devnest.report.entity.ReportTargetType;
import com.devnest.report.entity.ReportType;
import com.devnest.report.repository.ReportRepository;
import com.devnest.user.repository.UserRepository;
import com.devnest.notice.service.NotificationService;
import com.devnest.user.domain.User;
import com.devnest.user.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * 신고 생성 (알림 연동)
     */
    public Long createReport(Long reporterId, ReportCreateRequestDto requestDto) {
        // 신고자와 피신고자가 같은지 확인
        if (reporterId.equals(requestDto.getReportedUserId())) {
            throw new IllegalArgumentException("자신을 신고할 수 없습니다.");
        }

        // 피신고자가 존재하는지 확인
        if (!userRepository.existsById(requestDto.getReportedUserId())) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        // 신고 엔티티 생성
        Report report = Report.builder()
                .reporterId(reporterId)
                .reportedUserId(requestDto.getReportedUserId())
                .targetType(requestDto.getTargetType())
                .targetId(requestDto.getTargetId())
                .reportType(requestDto.getReportType())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .status(ReportStatus.PENDING)
                .build();

        Report savedReport = reportRepository.save(report);

        // 알림 생성
        createReportNotifications(savedReport);

        log.info("신고 생성 완료 - 신고 ID: {}, 신고자: {}, 피신고자: {}",
                savedReport.getReportId(), reporterId, requestDto.getReportedUserId());

        return savedReport.getReportId();
    }

    /**
     * 신고 처리 (알림 연동)
     */
    public void processReport(Long reportId, Long adminId, ReportProcessRequestDto requestDto) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고입니다."));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 신고입니다.");
        }

        // 신고 상태 업데이트
        switch (requestDto.getStatus()) {
            case APPROVED:
                report.approve(adminId, requestDto.getAdminResponse());
                break;
            case REJECTED:
                report.reject(adminId, requestDto.getAdminResponse());
                break;
            case DISMISSED:
                report.dismiss(adminId, requestDto.getAdminResponse());
                break;
            default:
                throw new IllegalArgumentException("유효하지 않은 처리 상태입니다.");
        }

        reportRepository.save(report);

        // 알림 생성
        createReportProcessNotifications(report, adminId, requestDto);

        // 사용자 정지 처리가 요청된 경우
        if (requestDto.isSuspendUser() && requestDto.getSuspendDays() != null
                && requestDto.getStatus() == ReportStatus.APPROVED) {
            suspendReportedUser(report, adminId, requestDto);
        }

        log.info("신고 처리 완료 - 신고 ID: {}, 처리자: {}, 상태: {}",
                reportId, adminId, requestDto.getStatus());
    }

    /**
     * 신고 접수 시 알림 생성
     */
    private void createReportNotifications(Report report) {
        try {
            // 1. 신고자에게 접수 확인 알림
            String reporterMessage = String.format("신고가 성공적으로 접수되었습니다.\n신고 제목: %s\n처리까지 다소 시간이 소요될 수 있습니다.",
                    report.getTitle());
            notificationService.createSystemNotification(report.getReporterId(), reporterMessage);

            // 2. 모든 관리자에게 신고 접수 알림
            List<User> admins = userRepository.findByRole(Role.ADMIN);
            for (User admin : admins) {
                String adminMessage = String.format("새로운 신고가 접수되었습니다.\n신고 유형: %s\n신고 제목: %s\n처리가 필요합니다.",
                        report.getReportType().getDescription(), report.getTitle());
                notificationService.createReportNotification(admin.getUserId(), report.getTitle(),
                        report.getReportId());
            }

            log.info("신고 접수 알림 발송 완료 - 신고 ID: {}, 관리자 {}명에게 발송",
                    report.getReportId(), admins.size());

        } catch (Exception e) {
            log.error("신고 접수 알림 발송 중 오류 발생: {}", e.getMessage());
        }
    }

    /**
     * 신고 처리 완료 시 알림 생성
     */
    private void createReportProcessNotifications(Report report, Long adminId, ReportProcessRequestDto requestDto) {
        try {
            User admin = userRepository.findById(adminId).orElse(null);
            String adminNickname = (admin != null) ? admin.getNickname() : "관리자";

            // 1. 신고자에게 처리 결과 알림
            String reporterMessage = createReporterNotificationMessage(report, requestDto, adminNickname);
            notificationService.createSystemNotification(report.getReporterId(), reporterMessage);

            // 2. 신고가 승인된 경우, 피신고자에게 알림
            if (requestDto.getStatus() == ReportStatus.APPROVED) {
                String reportedUserMessage = String.format(
                        "회원님에 대한 신고가 승인되었습니다.\n신고 사유: %s\n관리자 메시지: %s\n커뮤니티 이용규칙을 준수해 주시기 바랍니다.",
                        report.getReportType().getDescription(), requestDto.getAdminResponse());
                notificationService.createSystemNotification(report.getReportedUserId(), reportedUserMessage);
            }

            log.info("신고 처리 알림 발송 완료 - 신고 ID: {}, 처리 상태: {}",
                    report.getReportId(), requestDto.getStatus());

        } catch (Exception e) {
            log.error("신고 처리 알림 발송 중 오류 발생: {}", e.getMessage());
        }
    }

    /**
     * 신고자에게 보낼 처리 결과 메시지 생성
     */
    private String createReporterNotificationMessage(Report report, ReportProcessRequestDto requestDto,
            String adminNickname) {
        String statusText;
        switch (requestDto.getStatus()) {
            case APPROVED:
                statusText = "승인되었습니다";
                break;
            case REJECTED:
                statusText = "거부되었습니다";
                break;
            case DISMISSED:
                statusText = "기각되었습니다";
                break;
            default:
                statusText = "처리되었습니다";
        }

        return String.format("회원님이 신고하신 내용이 %s.\n신고 제목: %s\n처리자: %s\n관리자 메시지: %s\n\n신고해 주셔서 감사합니다.",
                statusText, report.getTitle(), adminNickname, requestDto.getAdminResponse());
    }

    /**
     * 피신고자 정지 처리 및 알림
     */
    private void suspendReportedUser(Report report, Long adminId, ReportProcessRequestDto requestDto) {
        try {
            User reportedUser = userRepository.findById(report.getReportedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("피신고자를 찾을 수 없습니다."));

            // 사용자 정지 처리
            String suspendReason = String.format("신고 승인으로 인한 정지 (신고 ID: %d, 사유: %s)",
                    report.getReportId(), report.getReportType().getDescription());
            reportedUser.suspend(requestDto.getSuspendDays(), suspendReason, adminId);
            userRepository.save(reportedUser);

            // 정지 알림 생성
            notificationService.createSuspendNotification(
                    report.getReportedUserId(),
                    suspendReason,
                    reportedUser.getSuspendedUntil());

            log.info("사용자 정지 처리 완료 - 사용자 ID: {}, 정지 기간: {}일, 신고 ID: {}",
                    report.getReportedUserId(), requestDto.getSuspendDays(), report.getReportId());

        } catch (Exception e) {
            log.error("사용자 정지 처리 중 오류 발생: {}", e.getMessage());
        }
    }

    /**
     * 신고 목록 조회 (관리자용)
     */
    @Transactional(readOnly = true)
    public Page<ReportListResponseDto> getReports(Pageable pageable) {
        return reportRepository.findAll(pageable)
                .map(ReportListResponseDto::fromEntity);
    }

    /**
     * 상태별 신고 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ReportListResponseDto> getReportsByStatus(ReportStatus status, Pageable pageable) {
        return reportRepository.findByStatus(status, pageable)
                .map(ReportListResponseDto::fromEntity);
    }

    /**
     * 신고 검색
     */
    @Transactional(readOnly = true)
    public Page<ReportListResponseDto> searchReports(String keyword, ReportStatus status,
            ReportType reportType, ReportTargetType targetType,
            Pageable pageable) {
        return reportRepository.searchReports(keyword, status, reportType, targetType, pageable)
                .map(ReportListResponseDto::fromEntity);
    }

    /**
     * 신고 상세 조회
     */
    @Transactional(readOnly = true)
    public ReportListResponseDto getReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고입니다."));

        return ReportListResponseDto.fromEntity(report);
    }

    /**
     * 사용자가 신고한 내역 조회
     */
    @Transactional(readOnly = true)
    public Page<ReportListResponseDto> getReportsByReporter(Long reporterId, Pageable pageable) {
        return reportRepository.findByReporterId(reporterId, pageable)
                .map(ReportListResponseDto::fromEntity);
    }

    /**
     * 사용자가 신고당한 내역 조회
     */
    @Transactional(readOnly = true)
    public Page<ReportListResponseDto> getReportsByReportedUser(Long reportedUserId, Pageable pageable) {
        return reportRepository.findByReportedUserId(reportedUserId, pageable)
                .map(ReportListResponseDto::fromEntity);
    }

    /**
     * 최근 신고 조회 (대시보드용)
     */
    @Transactional(readOnly = true)
    public List<ReportListResponseDto> getRecentReports() {
        return reportRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(ReportListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 신고 통계 조회
     */
    @Transactional(readOnly = true)
    public ReportStatsResponseDto getReportStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime weekStart = now.minusDays(7);
        LocalDateTime monthStart = now.minusDays(30);

        ReportStatsResponseDto stats = new ReportStatsResponseDto();

        // 전체 통계
        stats.setTotalReports(reportRepository.count());
        stats.setPendingReports(reportRepository.countByStatus(ReportStatus.PENDING));
        stats.setApprovedReports(reportRepository.countByStatus(ReportStatus.APPROVED));
        stats.setRejectedReports(reportRepository.countByStatus(ReportStatus.REJECTED));
        stats.setDismissedReports(reportRepository.countByStatus(ReportStatus.DISMISSED));

        // 기간별 통계
        stats.setTodayReports(reportRepository.countByCreatedAtAfter(todayStart));
        stats.setWeeklyReports(reportRepository.countByCreatedAtAfter(weekStart));
        stats.setMonthlyReports(reportRepository.countByCreatedAtAfter(monthStart));

        // 신고 유형별 통계
        stats.setSpamReports(reportRepository.countByReportType(ReportType.SPAM));
        stats.setInappropriateReports(reportRepository.countByReportType(ReportType.INAPPROPRIATE_CONTENT));
        stats.setHarassmentReports(reportRepository.countByReportType(ReportType.HARASSMENT));
        stats.setFakeInfoReports(reportRepository.countByReportType(ReportType.FAKE_INFORMATION));
        stats.setCopyrightReports(reportRepository.countByReportType(ReportType.COPYRIGHT_VIOLATION));
        stats.setPrivacyReports(reportRepository.countByReportType(ReportType.PRIVACY_VIOLATION));
        stats.setHateSpeechReports(reportRepository.countByReportType(ReportType.HATE_SPEECH));
        stats.setViolenceReports(reportRepository.countByReportType(ReportType.VIOLENCE));
        stats.setSexualReports(reportRepository.countByReportType(ReportType.SEXUAL_CONTENT));
        stats.setOtherReports(reportRepository.countByReportType(ReportType.OTHER));

        return stats;
    }

    /**
     * 미처리 신고 수 조회
     */
    @Transactional(readOnly = true)
    public long getPendingReportsCount() {
        return reportRepository.countPendingReports();
    }

    /**
     * 특정 사용자의 신고당한 횟수 조회
     */
    @Transactional(readOnly = true)
    public long getApprovedReportsCountByUser(Long userId) {
        return reportRepository.countApprovedReportsByUserId(userId);
    }
}