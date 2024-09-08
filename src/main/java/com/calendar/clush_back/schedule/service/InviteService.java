package com.calendar.clush_back.schedule.service;

import com.calendar.clush_back.auth.dto.UserResponseDTO;
import com.calendar.clush_back.auth.entity.User;
import com.calendar.clush_back.common.exception.CustomException;
import com.calendar.clush_back.common.request.PagingRequestDto;
import com.calendar.clush_back.common.response.CustomResponse;
import com.calendar.clush_back.schedule.dto.GroupInvitationResponseDto;
import com.calendar.clush_back.schedule.dto.UserGroupDto;
import com.calendar.clush_back.schedule.entity.CalendarGroup;
import com.calendar.clush_back.schedule.entity.GroupInvitation;
import com.calendar.clush_back.schedule.repository.GroupInvitationRepository;
import com.calendar.clush_back.schedule.repository.CalendarGroupRepository;
import com.calendar.clush_back.schedule.validator.UserGroupValidator;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.calendar.clush_back.auth.repository.UserRepository;

@Service
public class InviteService {

    private final CalendarGroupRepository calendarGroupRepository;
    private final GroupInvitationRepository groupInvitationRepository;
    private final UserRepository userRepository;
    private final UserGroupValidator userGroupValidator;

    public InviteService(CalendarGroupRepository calendarGroupRepository,
        GroupInvitationRepository groupInvitationRepository, UserRepository userRepository,
        UserGroupValidator userGroupValidator) {
        this.calendarGroupRepository = calendarGroupRepository;
        this.groupInvitationRepository = groupInvitationRepository;
        this.userRepository = userRepository;
        this.userGroupValidator = userGroupValidator;
    }

    // 특정 사용자에게 온 초대 목록 조회
    public CustomResponse<List<GroupInvitationResponseDto>> getInvitations(UserDetails userDetails,
        PagingRequestDto pagingRequestDto) {

        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        Pageable pageable = PageRequest.of(pagingRequestDto.getPage(), pagingRequestDto.getSize());

        List<GroupInvitation> invitations = groupInvitationRepository.findInvitationsByUserEmail(
            user.getEmail(), pageable);

        if (invitations.isEmpty()) {
            return CustomResponse.success("사용자의 초대 목록이 비어있습니다.", Collections.emptyList(), 204);
        }

        List<GroupInvitationResponseDto> dtos = invitations.stream().map(invitation -> {
            GroupInvitationResponseDto dto = new GroupInvitationResponseDto();
            dto.setId(invitation.getId());
            dto.setGroupId(invitation.getCalendarGroup().getId());
            dto.setGroupName(invitation.getCalendarGroup().getName());
            return dto;
        }).collect(Collectors.toList());

        return CustomResponse.success("나에게 온 초대 목록 조회 성공", dtos, 200);
    }

    // 그룹 초대 전송
    public CustomResponse<Void> inviteUserToGroup(Long calendarGroupId, UserDetails userDetails,
        Long inviteeId) {

        UserGroupDto userGroupDto = userGroupValidator.checkMemberValidity(userDetails,
            calendarGroupId);

        User invitee = userRepository.findById(inviteeId)
            .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        GroupInvitation invitation = new GroupInvitation(userGroupDto.getCalendarGroup(), invitee);
        groupInvitationRepository.save(invitation);

        return CustomResponse.success("초대 전송 성공", null, 204);
    }

    // 초대 수락
    public CustomResponse<Void> acceptInvitation(Long invitationId, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        GroupInvitation invitation = groupInvitationRepository.findById(invitationId)
            .orElseThrow(() -> new CustomException("초대장을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (!invitation.getInvitedUser().equals(user)) {
            throw new CustomException("그룹에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        CalendarGroup calendarGroup = invitation.getCalendarGroup();
        calendarGroup.getUsers().add(user);
        calendarGroupRepository.save(calendarGroup);

        groupInvitationRepository.delete(invitation);

        return CustomResponse.success("초대 수락 성공", null, 204);
    }

    // 초대 거절
    public CustomResponse<Void> declineInvitation(Long invitationId, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        GroupInvitation invitation = groupInvitationRepository.findById(invitationId)
            .orElseThrow(() -> new CustomException("초대장을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (!invitation.getInvitedUser().equals(user)) {
            throw new CustomException("그룹에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        groupInvitationRepository.delete(invitation);
        return CustomResponse.success("초대 거절 성공", null, 204);
    }

    // 그룹에서 멤버 제거
    public CustomResponse<Void> removeUserFromGroup(Long calendarGroupId, UserDetails userDetails,
        Long userId) {

        UserGroupDto userGroupDto = userGroupValidator.checkOwnerValidity(userDetails,
            calendarGroupId);

        CalendarGroup calendarGroup = userGroupDto.getCalendarGroup();

        User removeMember = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        calendarGroup.getUsers().remove(removeMember);
        calendarGroupRepository.save(calendarGroup);

        return CustomResponse.success("멤버 삭제 성공", null, 204);
    }

    // 그룹에 속해있는 멤버 조회
    public CustomResponse<List<UserResponseDTO>> getGroupUsers(Long calendarGroupId,
        UserDetails userDetails, PagingRequestDto pagingRequestDto) {

        Pageable pageable = PageRequest.of(pagingRequestDto.getPage(), pagingRequestDto.getSize());

        UserGroupDto userGroupDto = userGroupValidator.checkMemberValidity(userDetails,
            calendarGroupId);

        List<User> usersInGroup = calendarGroupRepository.findUsersByGroupId(calendarGroupId,
            pageable);

        if (usersInGroup == null || usersInGroup.isEmpty()) {
            return CustomResponse.success("그룹에 속한 사용자가 없습니다.", null, 204);
        }

        List<UserResponseDTO> userResponseDTOs = usersInGroup.stream().map(groupUser -> {
            UserResponseDTO dto = new UserResponseDTO();
            dto.setId(groupUser.getId());
            dto.setEmail(groupUser.getEmail());
            dto.setUserRoles(groupUser.getUserRoles());
            return dto;
        }).collect(Collectors.toList());

        return CustomResponse.success("그룹에 속해 있는 유저 조회 성공", userResponseDTOs, 200);
    }

    // 초대 코드를 이용한 그룹 가입
    public CustomResponse<String> joinGroup(String inviteCode, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        CalendarGroup calendarGroup = calendarGroupRepository.findByInviteCode(inviteCode)
            .orElseThrow(() -> new CustomException("유효하지 않은 초대 코드입니다.", HttpStatus.UNPROCESSABLE_ENTITY));

        // 이미 그룹 멤버인지 확인
        if (calendarGroup.getUsers().contains(user)) {
            throw new CustomException("이미 그룹에 가입되어 있습니다.", HttpStatus.CONFLICT);
        }

        calendarGroup.getUsers().add(user);
        calendarGroupRepository.save(calendarGroup);

        return CustomResponse.success("그룹에 성공적으로 가입하였습니다.", null, 200);
    }

}

