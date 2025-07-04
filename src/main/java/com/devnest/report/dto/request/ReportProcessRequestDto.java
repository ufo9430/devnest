package com.devnest.report.dto.request;

import com.devnest.report.entity.ReportStatus;
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
public class ReportProcessRequestDto {

    @NotNull(message = "처리 상태는 필수입니다.")
    private ReportStatus status;

    @NotBlank(message = "관리자 응답은 필수입니다.")
    @Size(min = 5, max = 1000, message = "관리자 응답은 5자 이상 1000자 이하여야 합니다.")
    private String adminResponse;

    private boolean suspendUser = false; // 사용자 정지 여부
    private Integer suspendDays; // 정지 기간 (일)
}