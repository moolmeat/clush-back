package com.calendar.clush_back.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "그룹 초대 응답 DTO")
public class GroupInvitationResponseDto {

    @Schema(description = "초대 ID", example = "1")
    private Long id;

    @Schema(description = "그룹 ID", example = "101")
    private Long groupId;

    @Schema(description = "그룹 이름", example = "My Group")
    private String groupName;
}