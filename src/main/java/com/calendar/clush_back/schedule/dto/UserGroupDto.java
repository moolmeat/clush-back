package com.calendar.clush_back.schedule.dto;

import com.calendar.clush_back.auth.entity.User;
import com.calendar.clush_back.schedule.entity.CalendarGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "사용자 그룹 DTO")
public class UserGroupDto {

    @Schema(description = "사용자 정보")
    private User user;

    @Schema(description = "캘린더 그룹 정보")
    private CalendarGroup calendarGroup;
}
