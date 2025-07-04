package com.devnest.report.dto.request;

import com.devnest.report.entity.ReportTargetType;
import com.devnest.report.entity.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportCreateRequestDto {

    @NotNull(message = "피신고자 ID는 필수입니다.")
    private Long reportedUserId;

    @NotNull(message = "신고 대상 유형은 필수입니다.")
    private ReportTargetType targetType;

    private Long targetId; // 게시글 ID, 댓글 ID 등 (사용자 신고시에는 null 가능)

    @NotNull(message = "신고 유형은 필수입니다.")
    private ReportType reportType;

    @NotBlank(message = "신고 제목은 필수입니다.")
    @Size(min = 5, max = 200, message = "신고 제목은 5자 이상 200자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "신고 내용은 필수입니다.")
    @Size(min = 10, max = 1000, message = "신고 내용은 10자 이상 1000자 이하여야 합니다.")
    private String content;
}