package com.calendar.clush_back.schedule.repository;

import com.calendar.clush_back.schedule.entity.CalendarGroup;
import com.calendar.clush_back.schedule.entity.Schedule;
import java.time.LocalDate;
import java.util.List;

public interface CustomScheduleRepository {

    List<Schedule> findAllByGroupAndDateRange(CalendarGroup calendarGroup, LocalDate fromDate,
        LocalDate toDate);

    List<Schedule> findSchedulesWithRecurrenceByGroup(CalendarGroup calendarGroup);
}
