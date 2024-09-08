package com.calendar.clush_back.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import com.calendar.clush_back.auth.entity.UserRole;

@Data
public class UserResponseDTO {

    @Schema(description = "사용자의 ID", example = "1")
    private Long id;

    @Schema(description = "사용자의 이메일", example = "john.doe@example.com")
    private String email;

    @Schema(description = "사용자에게 할당된 역할 목록")
    private List<UserRole> userRoles;

}
