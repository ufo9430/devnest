package com.devnest.topic.dto;

import com.devnest.topic.domain.Report;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequestDto {

    @NotNull(message = "신고 대상 ID는 필수입니다.")
    private Long targetId;

    @NotBlank(message = "신고 사유는 필수입니다.")
    private String reason;

    @NotNull(message = "targetType은 필수입니다.")
    private Report.TargetType targetType; // "TOPIC" 또는 "ANSWER"
}