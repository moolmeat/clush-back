package com.calendar.clush_back.schedule.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ScheduleStatusDto {
    private LocalDate date;
    private String status;
}
