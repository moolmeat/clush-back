package com.calendar.clush_back.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;

@Data
@Schema(description = "반복 일정 예외 처리 DTO")
public class RecurrenceDto {

    @Schema(description = "예외 날짜", example = "2024-10-01")
    private LocalDate exceptionDate;

    @Schema(description = "예외 유형 (EXCLUDE 또는 MODIFY)", example = "MODIFY")
    private String exceptionType;  // "EXCLUDE" or "MODIFY"

    @Schema(description = "수정된 제목", example = "수정된 제목입니다.")
    private String modifiedTitle;

    @Schema(description = "수정된 내용", example = "수정된 내용입니다.")
    private String modifiedContent;
}