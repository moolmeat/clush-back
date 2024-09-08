package com.calendar.clush_back.schedule.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScheduleDto {

    private Long id;
    private String title;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String recurrenceType;
    private String daysOfWeek;

    public ScheduleDto(Long id, String title, String content, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, String recurrenceType, String daysOfWeek) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.recurrenceType = recurrenceType;
        this.daysOfWeek = daysOfWeek;
    }
}
