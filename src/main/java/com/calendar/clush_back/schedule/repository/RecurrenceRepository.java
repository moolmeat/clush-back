package com.calendar.clush_back.schedule.repository;

import com.calendar.clush_back.schedule.entity.Recurrence;
import com.calendar.clush_back.schedule.entity.Schedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurrenceRepository extends JpaRepository<Recurrence, Long> {
    List<Recurrence> findAllBySchedule(Schedule schedule);
}