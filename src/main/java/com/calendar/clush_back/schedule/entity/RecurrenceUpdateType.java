package com.calendar.clush_back.schedule.entity;

import com.calendar.clush_back.common.exception.CustomException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.http.HttpStatus;

public enum RecurrenceUpdateType {
    EXCLUDE("EXCLUDE"),
    MODIFY("MODIFY");

    private final String value;

    RecurrenceUpdateType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RecurrenceUpdateType fromValue(String value) {
        for (RecurrenceUpdateType type : RecurrenceUpdateType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new CustomException("유효하지 않은 이넘 값입니다.", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
