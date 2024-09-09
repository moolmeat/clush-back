package com.calendar.clush_back.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "캘린더 그룹 DTO")
public class CalendarGroupDto {

    @Schema(description = "캘린더 그룹의 고유 ID", example = "1")
    private Long id;

    @Schema(description = "캘린더 그룹의 이름", example = "크러시 홍보 1팀")
    private String name;

    @Schema(description = "그룹에 속한 사용자들의 ID 목록", example = "[1001, 1002, 1003]")
    private List<Long> userIds;

    @Schema(description = "그룹에 참여하기 위한 초대 코드", example = "ABCD1234-ASD-123")
    private String inviteCode;

    public CalendarGroupDto(Long id, String name, List<Long> userIds, String inviteCode) {
        this.id = id;
        this.name = name;
        this.userIds = userIds;
        this.inviteCode = inviteCode;
    }
}