package com.calendar.clush_back.schedule.dto;

import com.calendar.clush_back.schedule.entity.ScheduleDayOfWeek;
import com.calendar.clush_back.schedule.entity.RecurrenceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "일정 DTO")
public class ScheduleDto {

    @Schema(description = "일정 ID", example = "1001")
    private Long id;

    @Schema(description = "일정 제목", example = "팀 미팅")
    private String title;

    @Schema(description = "일정 내용", example = "클러쉬 캘린더웹 회의")
    private String content;

    @Schema(description = "시작 날짜", example = "2024-09-01")
    private LocalDate startDate;

    @Schema(description = "종료 날짜", example = "2024-09-01")
    private LocalDate endDate;

    @Schema(description = "시작 시간", example = "09:00")
    private LocalTime startTime;

    @Schema(description = "종료 시간", example = "10:00")
    private LocalTime endTime;

    @Schema(description = "반복 유형 (DAILY, WEEKLY, MONTHLY, YEARLY)", example = "WEEKLY")
    private RecurrenceType recurrenceType;

    @Schema(description = "반복 요일 (MO, TU, WE TH, FR, SA, SU)", example = "MO, WE, FR")
    private List<ScheduleDayOfWeek> daysOfWeek;

    public ScheduleDto(Long id, String title, String content, LocalDate startDate,
        LocalDate endDate,
        LocalTime startTime, LocalTime endTime, RecurrenceType recurrenceType,
        List<ScheduleDayOfWeek> daysOfWeek) {
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