package com.calendar.clush_back.schedule.repository;

import com.calendar.clush_back.schedule.entity.Recurrence;
import com.calendar.clush_back.schedule.entity.Schedule;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CustomRecurrenceRepository {

    List<Recurrence> findAllBySchedule(Schedule schedule);

    Optional<Recurrence> findByScheduleAndExceptionDate(Schedule schedule, LocalDate exceptionDate);
}
