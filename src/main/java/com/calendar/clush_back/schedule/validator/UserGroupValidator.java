package com.calendar.clush_back.schedule.validator;

import com.calendar.clush_back.auth.entity.User;
import com.calendar.clush_back.auth.repository.UserRepository;
import com.calendar.clush_back.common.exception.CustomException;
import com.calendar.clush_back.schedule.dto.UserGroupDto;
import com.calendar.clush_back.schedule.entity.CalendarGroup;
import com.calendar.clush_back.schedule.entity.Schedule;
import com.calendar.clush_back.schedule.repository.CalendarGroupRepository;
import com.calendar.clush_back.schedule.repository.ScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserGroupValidator {

    private final UserRepository userRepository;

    private final CalendarGroupRepository calendarGroupRepository;

    private final ScheduleRepository scheduleRepository;

    public UserGroupValidator(CalendarGroupRepository calendarGroupRepository,
        UserRepository userRepository, ScheduleRepository scheduleRepository) {
        this.calendarGroupRepository = calendarGroupRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public UserGroupDto checkMemberValidity(UserDetails userDetails, Long calendarGroupId) {

        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        CalendarGroup calendarGroup = calendarGroupRepository.findById(calendarGroupId)
            .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (!calendarGroup.getUsers().contains(user)) {
            throw new CustomException("그룹에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        return UserGroupDto.builder()
            .user(user)
            .calendarGroup(calendarGroup)
            .build();
    }

    public UserGroupDto checkOwnerValidity(UserDetails userDetails, Long calendarGroupId) {

        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        CalendarGroup calendarGroup = calendarGroupRepository.findById(calendarGroupId)
            .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (!calendarGroup.getOwner().equals(user)) {
            throw new CustomException("그룹에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        return UserGroupDto.builder()
            .user(user)
            .calendarGroup(calendarGroup)
            .build();
    }

    public Schedule checkScheduleValidity(UserDetails userDetails, Long scheduleId) {

        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new CustomException("스케줄을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (!schedule.getCalendarGroup().getUsers().contains(user)) {
            throw new CustomException("스케쥴에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        return schedule;
    }
}
