package com.calendar.clush_back.schedule.repository;

import com.calendar.clush_back.schedule.entity.GroupInvitation;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomGroupInvitationRepository {
    List<GroupInvitation> findInvitationsByUserEmail(String email, Pageable pageable);
}
