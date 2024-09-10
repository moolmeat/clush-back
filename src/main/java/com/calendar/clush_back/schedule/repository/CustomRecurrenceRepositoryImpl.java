package com.calendar.clush_back.schedule.repository;

import static com.calendar.clush_back.schedule.entity.QRecurrence.recurrence;

import com.calendar.clush_back.schedule.entity.Recurrence;
import com.calendar.clush_back.schedule.entity.Schedule;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomRecurrenceRepositoryImpl implements CustomRecurrenceRepository {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public CustomRecurrenceRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Recurrence> findAllBySchedule(Schedule schedule) {
        return queryFactory
            .selectFrom(recurrence)
            .where(recurrence.schedule.eq(schedule))
            .fetch();
    }

    @Override
    public Optional<Recurrence> findByScheduleAndExceptionDate(Schedule schedule,
        LocalDate exceptionDate) {
        Recurrence result = queryFactory
            .selectFrom(recurrence)
            .where(
                recurrence.schedule.eq(schedule)
                    .and(recurrence.exceptionDate.eq(exceptionDate))
            )
            .fetchOne();
        return Optional.ofNullable(result);
    }
}
