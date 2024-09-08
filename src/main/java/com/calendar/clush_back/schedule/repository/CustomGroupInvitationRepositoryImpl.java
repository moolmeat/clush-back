package com.calendar.clush_back.schedule.repository;

import com.calendar.clush_back.schedule.entity.GroupInvitation;
import com.calendar.clush_back.schedule.entity.QGroupInvitation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CustomGroupInvitationRepositoryImpl implements CustomGroupInvitationRepository {

    private final JPAQueryFactory queryFactory;

    public CustomGroupInvitationRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    // 특정 사용자에 대한 초대 목록 조회
    @Override
    public List<GroupInvitation> findInvitationsByUserEmail(String email, Pageable pageable) {
        QGroupInvitation qGroupInvitation = QGroupInvitation.groupInvitation;

        return queryFactory
            .selectFrom(qGroupInvitation)
            .where(qGroupInvitation.invitedUser.email.eq(email))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }
}
