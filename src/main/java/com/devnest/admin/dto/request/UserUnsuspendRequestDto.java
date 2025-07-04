package com.devnest.admin.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUnsuspendRequestDto {

    @NotNull(message = "관리자 ID는 필수입니다.")
    private Long adminId;

    private String reason; // 정지 해제 사유 (선택사항)
}