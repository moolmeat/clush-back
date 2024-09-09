package com.calendar.clush_back.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;

@Data
@Schema(description = "스케줄 조회 범위 DTO")
public class GetScheduleDto {

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "시작 날짜", example = "2024-09-01")
    private LocalDate fromDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "종료 날짜", example = "2024-09-30")
    private LocalDate toDate;
}