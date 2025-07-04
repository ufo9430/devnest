package com.devnest.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportStatsResponseDto {

    private long totalReports; // 총 신고 수
    private long pendingReports; // 처리 대기 중인 신고 수
    private long approvedReports; // 승인된 신고 수
    private long rejectedReports; // 거부된 신고 수
    private long dismissedReports; // 기각된 신고 수
    private long todayReports; // 오늘 접수된 신고 수
    private long weeklyReports; // 이번 주 접수된 신고 수
    private long monthlyReports; // 이번 달 접수된 신고 수

    // 신고 유형별 통계
    private long spamReports; // 스팸/광고 신고 수
    private long inappropriateReports; // 부적절한 내용 신고 수
    private long harassmentReports; // 괴롭힘/욕설 신고 수
    private long fakeInfoReports; // 허위정보 신고 수
    private long copyrightReports; // 저작권 침해 신고 수
    private long privacyReports; // 개인정보 침해 신고 수
    private long hateSpeechReports; // 혐오 발언 신고 수
    private long violenceReports; // 폭력적 내용 신고 수
    private long sexualReports; // 성적 내용 신고 수
    private long otherReports; // 기타 신고 수
}