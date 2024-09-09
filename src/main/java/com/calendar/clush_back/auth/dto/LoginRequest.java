package com.calendar.clush_back.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "로그인 DTO")
public class LoginRequest {

    @Schema(description = "사용자의 이메일")
    private String email;

    @Schema(description = "사용자의 비밀번호")
    private String password;

}