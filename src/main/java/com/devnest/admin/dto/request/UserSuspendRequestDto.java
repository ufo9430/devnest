package com.devnest.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSuspendRequestDto {

    @NotNull(message = "정지 기간은 필수입니다.")
    @Positive(message = "정지 기간은 양수여야 합니다.")
    private Integer days;

    @NotBlank(message = "정지 사유는 필수입니다.")
    @Size(min = 5, max = 1000, message = "정지 사유는 5자 이상 1000자 이하여야 합니다.")
    private String reason;

    @NotNull(message = "관리자 ID는 필수입니다.")
    private Long adminId;
}