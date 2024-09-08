package com.calendar.clush_back.schedule.repository;

import com.calendar.clush_back.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, CustomScheduleRepository {
}
