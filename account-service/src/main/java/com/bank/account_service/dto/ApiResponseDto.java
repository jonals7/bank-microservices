package com.bank.account_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto<T> {

    private int status;
    private String message;
    private T data;

    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<>(200, "Success", data);
    }

    public static <T> ApiResponseDto<T> created(T data) {
        return new ApiResponseDto<>(201, "Created successfully", data);
    }

    public static <T> ApiResponseDto<T> error(int status, String message) {
        return new ApiResponseDto<>(status, message, null);
    }
}