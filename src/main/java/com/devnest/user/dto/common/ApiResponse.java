package com.devnest.user.dto.common;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(message, null);
    }
}

//공통 응답 DTO 입니다!
