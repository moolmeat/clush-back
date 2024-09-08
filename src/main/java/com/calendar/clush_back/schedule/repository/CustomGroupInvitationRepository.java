package com.calendar.clush_back.schedule.repository;

import com.calendar.clush_back.schedule.entity.GroupInvitation;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomGroupInvitationRepository {
    // 특정 사용자에 대한 모든 초대 조회
    List<GroupInvitation> findInvitationsByUserEmail(String email, Pageable pageable);
}
