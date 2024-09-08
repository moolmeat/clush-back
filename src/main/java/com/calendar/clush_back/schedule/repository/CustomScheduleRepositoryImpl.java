package com.calendar.clush_back.schedule.repository;

import com.calendar.clush_back.schedule.entity.CalendarGroup;
import com.calendar.clush_back.schedule.entity.QSchedule;
import com.calendar.clush_back.schedule.entity.Schedule;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class CustomScheduleRepositoryImpl implements CustomScheduleRepository {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public CustomScheduleRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    // 그룹과 기간을 기준으로 모든 스케줄을 조회하는 메서드 (QueryDSL 사용)
    @Override
    public List<Schedule> findAllByGroupAndDateRange(CalendarGroup calendarGroup,
        LocalDate fromDate, LocalDate toDate) {
        QSchedule schedule = QSchedule.schedule;

        return queryFactory.selectFrom(schedule)
            .where(schedule.calendarGroup.eq(calendarGroup)
                .and(schedule.startDate.between(fromDate, toDate)
                    .or(schedule.endDate.between(fromDate, toDate))))
            .fetch();
    }

    @Override
    public List<Schedule> findSchedulesWithRecurrenceByGroup(CalendarGroup calendarGroup) {
        QSchedule schedule = QSchedule.schedule;

        return queryFactory
            .selectFrom(schedule)
            .where(schedule.calendarGroup.eq(calendarGroup)
                .and(schedule.recurrenceType.isNotNull()))
            .fetch();
    }
}
