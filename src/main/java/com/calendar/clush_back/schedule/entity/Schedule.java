package com.calendar.clush_back.schedule.entity;

import jakarta.persistence.*;
import java.util.List;
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

    private String title;               // 제목
    private String content;             // 내용

    private LocalDate startDate;        // 시작일
    private LocalDate endDate;          // 종료일
    private LocalTime startTime;        // 시작 시간
    private LocalTime endTime;          // 종료 시간

    private RecurrenceType recurrenceType;      // 예외 타입 (DAILY, WEEKLY, MONTHLY, YEARLY)
    private List<ScheduleDayOfWeek> daysOfWeek;          // 요일 (타입이 WEEKLY 인 경우만)

    @ManyToOne
    @JoinColumn(name = "calendar_group_id")
    private CalendarGroup calendarGroup;

    public Schedule(String title, String content, LocalDate startDate, LocalDate endDate,
        LocalTime startTime, LocalTime endTime, RecurrenceType recurrenceType,
        List<ScheduleDayOfWeek> daysOfWeek, CalendarGroup calendarGroup) {
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
