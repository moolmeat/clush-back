package com.calendar.clush_back.schedule.repository;

import com.calendar.clush_back.schedule.entity.CalendarGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarGroupRepository extends JpaRepository<CalendarGroup, Long>,
    CustomCalendarGroupRepository {

}
