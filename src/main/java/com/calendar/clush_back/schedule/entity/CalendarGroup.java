package com.calendar.clush_back.schedule.entity;

import com.calendar.clush_back.auth.entity.User;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "calendar_group")
public class CalendarGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String inviteCode;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToMany
    @JoinTable(
        name = "calendar_group_users",
        joinColumns = @JoinColumn(name = "calendar_group_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    public CalendarGroup(String name, User owner, String inviteCode) {
        this.name = name;
        this.owner = owner;
        this.inviteCode = inviteCode;
    }
}
