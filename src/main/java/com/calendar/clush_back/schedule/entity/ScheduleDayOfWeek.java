package com.calendar.clush_back.schedule.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.calendar.clush_back.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public enum ScheduleDayOfWeek {
    MO("MO"), // Monday
    TU("TU"), // Tuesday
    WE("WE"), // Wednesday
    TH("TH"), // Thursday
    FR("FR"), // Friday
    SA("SA"), // Saturday
    SU("SU"); // Sunday

    private final String value;

    ScheduleDayOfWeek(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ScheduleDayOfWeek fromValue(String value) {
        for (ScheduleDayOfWeek day : ScheduleDayOfWeek.values()) {
            if (day.value.equalsIgnoreCase(value)) {
                return day;
            }
        }
        throw new CustomException("유효하지 않은 이넘 값입니다.", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}

