package com.calendar.clush_back.schedule.dto;

import java.util.List;
import lombok.Data;

@Data
public class CalendarGroupDto {

    private Long id;
    private String name;
    private List<Long> userIds;
    private String inviteCode;

    public CalendarGroupDto(Long id, String name, List<Long> userIds, String inviteCode) {
        this.id = id;
        this.name = name;
        this.userIds = userIds;
        this.inviteCode = inviteCode;
    }
}