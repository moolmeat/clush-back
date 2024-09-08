package com.calendar.clush_back.schedule.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class RecurrenceDto {
    private LocalDate exceptionDate;
    private String exceptionType;  // "EXCLUDE" or "MODIFY"
    private String modifiedTitle;
    private String modifiedContent;
}
