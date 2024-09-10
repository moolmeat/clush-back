package com.calendar.clush_back.schedule.mapper;

import com.calendar.clush_back.schedule.dto.ScheduleDto;
import com.calendar.clush_back.schedule.entity.CalendarGroup;
import com.calendar.clush_back.schedule.entity.Recurrence;
import com.calendar.clush_back.schedule.entity.Schedule;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Component;

@Component
public class ScheduleMapper {

    // 엔티티를 Dto로 변환
    public ScheduleDto toDto(Schedule schedule) {
        return new ScheduleDto(
            schedule.getId(),
            schedule.getTitle(),
            schedule.getContent(),
            schedule.getStartDate(),
            schedule.getEndDate(),
            schedule.getStartTime(),
            schedule.getEndTime(),
            schedule.getRecurrenceType(),
            schedule.getDaysOfWeek()
        );
    }

    public ScheduleDto toDto(Schedule schedule, LocalDate nextStartDate, LocalTime nextStartTime,
        LocalDate nextEndDate, LocalTime nextEndTime) {
        return new ScheduleDto(
            schedule.getId(),
            schedule.getTitle(),
            schedule.getContent(),
            nextStartDate,
            nextEndDate,
            nextStartTime,
            nextEndTime,
            schedule.getRecurrenceType(),
            schedule.getDaysOfWeek()
        );
    }

    // Recurrence을 사용하여 수정된 ScheduleDto를 생성
    public ScheduleDto toDto(Schedule schedule, Recurrence exception, LocalDate nextStartDate,
        LocalTime nextStartTime, LocalDate nextEndDate, LocalTime nextEndTime) {
        return new ScheduleDto(
            schedule.getId(),
            exception.getModifiedTitle(),
            exception.getModifiedContent(),
            nextStartDate,
            nextEndDate,
            nextStartTime,
            nextEndTime,
            schedule.getRecurrenceType(),
            schedule.getDaysOfWeek()
        );
    }

    // Dto를 엔티티로 변환
    public Schedule toEntity(ScheduleDto scheduleDto, CalendarGroup calendarGroup) {
        return new Schedule(
            scheduleDto.getTitle(),
            scheduleDto.getContent(),
            scheduleDto.getStartDate(),
            scheduleDto.getEndDate(),
            scheduleDto.getStartTime(),
            scheduleDto.getEndTime(),
            scheduleDto.getRecurrenceType(),
            scheduleDto.getDaysOfWeek(),
            calendarGroup
        );
    }
}
