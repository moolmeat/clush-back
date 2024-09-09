package com.calendar.clush_back.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "커스텀 응답 DTO")
public class CustomResponse<T> {

    @Schema(description = "응답 성공 여부 (success, failure)", example = "success")
    private boolean success;

    @Schema(description = "응답 메시지", example = "생성에 성공하였습니다.")
    private String message;

    @Schema(description = "응답 데이터", example = "UserResponseDto")
    private T data;

    @Schema(description = "HTTP 상태 코드", example = "200")
    private int statusCode;

    public static <T> CustomResponse<T> success(String message, T data, int statusCode) {
        return new CustomResponse<>(true, message, data, statusCode);
    }

    public static <T> CustomResponse<T> failure(String message, int statusCode) {
        return new CustomResponse<>(false, message, null, statusCode);
    }
}