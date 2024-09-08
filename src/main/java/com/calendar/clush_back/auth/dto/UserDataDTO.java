package com.calendar.clush_back.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.calendar.clush_back.auth.entity.UserRole;

@Data
@NoArgsConstructor
public class UserDataDTO {

    @Schema(description = "사용자의 이메일")
    private String email;

    @Schema(description = "사용자의 비밀번호")
    private String password;

    @Schema(description = "사용자에게 할당된 역할 목록")
    private List<UserRole> userRoles;

}
