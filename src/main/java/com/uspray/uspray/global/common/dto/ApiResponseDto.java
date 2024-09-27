package com.uspray.uspray.global.common.dto;

import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.SuccessStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponseDto<T> {

    private final int code;
    private final String message;
    private T data;

    public static ApiResponseDto<?> success(SuccessStatus successStatus) {
        return new ApiResponseDto<>(successStatus.getHttpStatus().value(),
            successStatus.getMessage());
    }

    public static <T> ApiResponseDto<T> success(SuccessStatus successStatus, T data) {
        return new ApiResponseDto<>(successStatus.getHttpStatus().value(),
            successStatus.getMessage(), data);
    }

    public static ApiResponseDto<?> error(ErrorStatus errorStatus) {
        return new ApiResponseDto<>(errorStatus.getHttpStatus().value(), errorStatus.getMessage());
    }

    public static ApiResponseDto<?> error(ErrorStatus errorStatus, String message) {
        return new ApiResponseDto<>(errorStatus.getHttpStatusCode(), message);
    }

    public static ApiResponseDto<?> error(Integer httpStatus, String message) {
        return new ApiResponseDto<>(httpStatus, message);
    }

}
