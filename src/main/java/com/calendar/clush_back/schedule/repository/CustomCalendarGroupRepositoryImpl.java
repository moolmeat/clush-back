package com.calendar.clush_back.schedule.repository;

import com.calendar.clush_back.auth.entity.QUser;
import com.calendar.clush_back.auth.entity.User;
import com.calendar.clush_back.schedule.entity.CalendarGroup;
import com.calendar.clush_back.schedule.entity.QCalendarGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class CustomCalendarGroupRepositoryImpl implements CustomCalendarGroupRepository {

    private final JPAQueryFactory queryFactory;

    public CustomCalendarGroupRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<CalendarGroup> findGroupsByUserEmail(String email, Pageable pageable) {
        QCalendarGroup qGroup = QCalendarGroup.calendarGroup;

        // QueryFactory로 페이징된 결과 조회
        return queryFactory
            .selectFrom(qGroup)
            .join(qGroup.users)
            .where(qGroup.users.any().email.eq(email))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(qGroup.id.desc())
            .fetch();
    }

    @Override
    public List<User> findUsersByGroupId(Long calendarGroupId, Pageable pageable) {
        QCalendarGroup qCalendarGroup = QCalendarGroup.calendarGroup;
        QUser qUser = QUser.user;

        // QueryDSL로 그룹에 속한 유저 목록 조회
        return queryFactory
            .select(qUser)
            .from(qCalendarGroup)
            .join(qCalendarGroup.users, qUser)
            .where(qCalendarGroup.id.eq(calendarGroupId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public Optional<CalendarGroup> findByInviteCode(String inviteCode) {
        QCalendarGroup qCalendarGroup = QCalendarGroup.calendarGroup;

        CalendarGroup result = queryFactory
            .selectFrom(qCalendarGroup)
            .where(qCalendarGroup.inviteCode.eq(inviteCode))
            .fetchOne();

        return Optional.ofNullable(result);
    }
}
