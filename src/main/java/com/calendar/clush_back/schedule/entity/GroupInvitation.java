package com.calendar.clush_back.schedule.entity;

import com.calendar.clush_back.auth.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "group_invitation")
public class GroupInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "calendar_group_id")
    private CalendarGroup calendarGroup;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User invitedUser;

    public GroupInvitation(CalendarGroup calendarGroup, User invitedUser) {
        this.calendarGroup = calendarGroup;
        this.invitedUser = invitedUser;
    }
}
