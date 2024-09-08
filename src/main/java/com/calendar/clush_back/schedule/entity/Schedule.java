package com.calendar.clush_back.schedule.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private String recurrenceType;
    private String daysOfWeek;

    @ManyToOne
    @JoinColumn(name = "calendar_group_id")
    private CalendarGroup calendarGroup;

    public Schedule(String title, String content, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, String recurrenceType, String daysOfWeek, CalendarGroup calendarGroup) {
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.recurrenceType = recurrenceType;
        this.daysOfWeek = daysOfWeek;
        this.calendarGroup = calendarGroup;
    }
}
