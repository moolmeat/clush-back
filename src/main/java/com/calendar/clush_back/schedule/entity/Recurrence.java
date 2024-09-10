package com.calendar.clush_back.schedule.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "recurrence")
public class Recurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate exceptionDate;      // 예외가 적용될 날짜
    private RecurrenceUpdateType updateType;         // "EXCLUDE" (제외) or "MODIFY" (수정)
    private String modifiedTitle;         // 수정된 제목 (선택 사항)
    private String modifiedContent;       // 수정된 내용 (선택 사항)

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;            // 원래 일정과의 관계

    public Recurrence(LocalDate exceptionDate, RecurrenceUpdateType updateType, Schedule schedule) {
        this.exceptionDate = exceptionDate;
        this.updateType = updateType;
        this.schedule = schedule;
    }
}
