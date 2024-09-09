package com.calendar.clush_back.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;

@Data
@Schema(description = "생리 일정 상태 DTO")
public class ScheduleStatusDto {

    @Schema(description = "상태 날짜", example = "2024-09-01")
    private LocalDate date;

    @Schema(description = "일정 상태(생리 중, 배란일, 가임기, 준비일)", example = "생리 중")
    private String status;
}