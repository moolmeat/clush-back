package com.calendar.clush_back.schedule.service;

import com.calendar.clush_back.auth.entity.User;
import com.calendar.clush_back.common.exception.CustomException;
import com.calendar.clush_back.common.request.PagingRequestDto;
import com.calendar.clush_back.common.response.CustomResponse;
import com.calendar.clush_back.schedule.dto.CalendarGroupDto;
import com.calendar.clush_back.schedule.entity.CalendarGroup;
import com.calendar.clush_back.schedule.mapper.CalendarGroupMapper;
import com.calendar.clush_back.schedule.repository.CalendarGroupRepository;
import java.util.Collections;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.calendar.clush_back.auth.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarGroupService {

    private final CalendarGroupRepository calendarGroupRepository;
    private final UserRepository userRepository;
    private final CalendarGroupMapper calendarGroupMapper;

    public CalendarGroupService(CalendarGroupRepository calendarGroupRepository,
        UserRepository userRepository, CalendarGroupMapper calendarGroupMapper) {
        this.calendarGroupRepository = calendarGroupRepository;
        this.userRepository = userRepository;
        this.calendarGroupMapper = calendarGroupMapper;
    }

    // 그룹 생성
    public CustomResponse<CalendarGroupDto> createGroup(String name, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        String inviteCode = generateInviteCode();

        CalendarGroup calendarGroup = new CalendarGroup(name, user, inviteCode);
        calendarGroup.getUsers().add(user);
        CalendarGroup savedCalendarGroup = calendarGroupRepository.save(calendarGroup);
        return CustomResponse.success("그룹 생성 성공", calendarGroupMapper.toDto(savedCalendarGroup),
            201);
    }

    // 사용자가 속한 모든 그룹 조회
    public CustomResponse<List<CalendarGroupDto>> getUserGroups(UserDetails userDetails,
        PagingRequestDto pagingRequestDto) {
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        Pageable pageable = PageRequest.of(pagingRequestDto.getPage(), pagingRequestDto.getSize());

        List<CalendarGroup> calendarGroups = calendarGroupRepository.findGroupsByUserEmail(
            user.getEmail(), pageable);

        if (calendarGroups.isEmpty()) {
            return CustomResponse.success("사용자가 속한 그룹이 없습니다", Collections.emptyList(), 204);
        }

        List<CalendarGroupDto> calendarGroupDtos = calendarGroups.stream()
            .map(calendarGroupMapper::toDto)
            .collect(Collectors.toList());

        return CustomResponse.success("사용자의 그룹 조회 성공", calendarGroupDtos, 200);
    }

    // 그룹 명 수정
    public CustomResponse<Void> updateGroupName(Long groupId, String newName,
        UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        CalendarGroup calendarGroup = getOwnedGroup(groupId, user);

        calendarGroup.setName(newName);
        calendarGroupRepository.save(calendarGroup);

        return CustomResponse.success("그룹 명 수정 성공", null, 204);
    }

    // 그룹 삭제
    public CustomResponse<Void> deleteGroup(Long groupId, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        CalendarGroup calendarGroup = getOwnedGroup(groupId, user);

        calendarGroupRepository.delete(calendarGroup);

        return CustomResponse.success("그룹 삭제 성공", null, 204);
    }

    private CalendarGroup getOwnedGroup(Long groupId, User user) {
        CalendarGroup calendarGroup = calendarGroupRepository.findById(groupId)
            .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        if (!calendarGroup.getOwner().equals(user)) {
            throw new CustomException("그룹에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        return calendarGroup;
    }

    // 난수 코드 생성 메서드
    private String generateInviteCode() {
        return UUID.randomUUID().toString();
    }
}
