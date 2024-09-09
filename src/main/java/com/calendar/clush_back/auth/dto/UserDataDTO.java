package com.calendar.clush_back.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.calendar.clush_back.auth.entity.UserRole;

@Data
@NoArgsConstructor
@Schema(description = "회원가입시 사용자 데이터 DTO")
public class UserDataDTO {

    @Schema(description = "사용자의 이메일", example = "woomoonsik@clush.com")
    private String email;

    @Schema(description = "사용자의 비밀번호 (8자 이상)", example = "clush123")
    private String password;

    @Schema(description = "사용자에게 할당된 역할 목록")
    private List<UserRole> userRoles;

}
