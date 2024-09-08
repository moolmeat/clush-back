package com.calendar.clush_back.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private int statusCode;

    public static <T> CustomResponse<T> success(String message, T data, int statusCode) {
        return new CustomResponse<>(true, message, data, statusCode);
    }

    public static <T> CustomResponse<T> failure(String message, int statusCode) {
        return new CustomResponse<>(false, message, null, statusCode);
    }
}
