package com.calendar.clush_back.schedule.repository;

import com.calendar.clush_back.schedule.entity.GroupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long>,
    CustomGroupInvitationRepository {

}
