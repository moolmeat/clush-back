package com.calendar.clush_back.schedule.mapper;

import com.calendar.clush_back.auth.entity.User;
import com.calendar.clush_back.schedule.dto.CalendarGroupDto;
import com.calendar.clush_back.schedule.entity.CalendarGroup;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CalendarGroupMapper {

    // 그룹 엔티티를 DTO로 변환하는 메서드
    public CalendarGroupDto toDto(CalendarGroup calendarGroup) {
        List<Long> userIds = calendarGroup.getUsers().stream()
            .map(User::getId)
            .collect(Collectors.toList());

        return new CalendarGroupDto(calendarGroup.getId(), calendarGroup.getName(), userIds,
            calendarGroup.getInviteCode());
    }
}
