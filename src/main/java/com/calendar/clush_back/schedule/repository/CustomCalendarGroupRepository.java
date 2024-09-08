package com.calendar.clush_back.schedule.repository;

import com.calendar.clush_back.auth.entity.User;
import com.calendar.clush_back.schedule.entity.CalendarGroup;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface CustomCalendarGroupRepository {
    List<CalendarGroup> findGroupsByUserEmail(String email, Pageable pageable);
    List<User> findUsersByGroupId(Long calendarGroupId, Pageable pageable);
    Optional<CalendarGroup> findByInviteCode(String inviteCode);
}
