package com.calendar.clush_back.schedule.dto;

import com.calendar.clush_back.auth.entity.User;
import com.calendar.clush_back.schedule.entity.CalendarGroup;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserGroupDto {

    private User user;

    private CalendarGroup calendarGroup;

}
