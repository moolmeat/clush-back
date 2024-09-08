package com.calendar.clush_back.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Data;

@Data
public class GetScheduleDto {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;
}
