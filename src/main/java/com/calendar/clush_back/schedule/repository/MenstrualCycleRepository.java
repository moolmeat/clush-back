package com.calendar.clush_back.schedule.repository;

import com.calendar.clush_back.schedule.entity.CalendarGroup;
import com.calendar.clush_back.schedule.entity.MenstrualCycle;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenstrualCycleRepository extends JpaRepository<MenstrualCycle, Long> {

    Optional<MenstrualCycle> findByCalendarGroup(CalendarGroup calendarGroup);
}