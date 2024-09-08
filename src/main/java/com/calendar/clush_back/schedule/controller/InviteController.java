package com.calendar.clush_back.schedule.controller;

import com.calendar.clush_back.auth.dto.UserResponseDTO;
import com.calendar.clush_back.auth.entity.User;
import com.calendar.clush_back.common.request.PagingRequestDto;
import com.calendar.clush_back.common.response.CustomResponse;
import com.calendar.clush_back.schedule.dto.GroupInvitationResponseDto;
import com.calendar.clush_back.schedule.entity.GroupInvitation;
import com.calendar.clush_back.schedule.service.InviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "그룹 초대 관리", description = "그룹 초대, 수락, 거절 및 멤버 관리 기능을 제공합니다.")
@RestController
@RequestMapping("/invitations")
public class InviteController {

    private final InviteService inviteService;

    public InviteController(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    @Operation(summary = "내 초대 목록 조회", description = "로그인한 사용자가 받은 그룹 초대 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "초대 목록 조회 성공"),
        @ApiResponse(responseCode = "204", description = "초대 목록이 비어있음"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),
    })
    @GetMapping
    public CustomResponse<List<GroupInvitationResponseDto>> getMyInvitations(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody PagingRequestDto pagingRequestDto) {
        return inviteService.getInvitations(userDetails, pagingRequestDto);
    }

    @Operation(summary = "그룹에 사용자 초대", description = "특정 사용자를 그룹에 초대합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "초대 전송 성공"),
        @ApiResponse(responseCode = "403", description = "접근이 거부됨"),
        @ApiResponse(responseCode = "404", description = "그룹이 존재하지 않음"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),
    })
    @PostMapping("/{calendarGroupId}/invite")
    public CustomResponse<Void> inviteUserToGroup(@PathVariable Long calendarGroupId,
        @RequestParam Long inviteeId, @AuthenticationPrincipal UserDetails userDetails) {
        return inviteService.inviteUserToGroup(calendarGroupId, userDetails, inviteeId);
    }

    @Operation(summary = "초대 수락", description = "그룹 초대를 수락합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "초대 수락 성공"),
        @ApiResponse(responseCode = "403", description = "접근이 거부됨"),
        @ApiResponse(responseCode = "404", description = "그룹이 존재하지 않음"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),
        @ApiResponse(responseCode = "404", description = "초대가 존재하지 않음"),
    })
    @PostMapping("{invitationId}/accept")
    public CustomResponse<Void> acceptInvitation(@PathVariable Long invitationId,
        @AuthenticationPrincipal UserDetails userDetails) {
        return inviteService.acceptInvitation(invitationId, userDetails);
    }

    @Operation(summary = "초대 거절", description = "그룹 초대를 거절합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "초대 거절 성공"),
        @ApiResponse(responseCode = "403", description = "접근이 거부됨"),
        @ApiResponse(responseCode = "404", description = "그룹이 존재하지 않음"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),
        @ApiResponse(responseCode = "404", description = "초대가 존재하지 않음"),
    })
    @PostMapping("{invitationId}/decline")
    public CustomResponse<Void> declineInvitation(@PathVariable Long invitationId,
        @AuthenticationPrincipal UserDetails userDetails) {
        return inviteService.declineInvitation(invitationId, userDetails);
    }

    @Operation(summary = "그룹 멤버 제거", description = "특정 사용자를 그룹에서 제거합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "멤버 제거 성공"),
        @ApiResponse(responseCode = "403", description = "접근이 거부됨"),
        @ApiResponse(responseCode = "404", description = "그룹이 존재하지 않음"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),
    })
    @DeleteMapping("/{calendarGroupId}/users")
    public CustomResponse<Void> removeUser(@PathVariable Long calendarGroupId,
        @RequestParam Long userId, @AuthenticationPrincipal UserDetails userDetails) {
        return inviteService.removeUserFromGroup(calendarGroupId, userDetails, userId);
    }

    @Operation(summary = "그룹 멤버 조회", description = "그룹에 속한 모든 멤버를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "그룹 멤버 조회 성공"),
        @ApiResponse(responseCode = "204", description = "그룹에 멤버가 없음"),
        @ApiResponse(responseCode = "403", description = "접근이 거부됨"),
        @ApiResponse(responseCode = "404", description = "그룹이 존재하지 않음"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),
    })
    @GetMapping("/{calendarGroupId}/users")
    public CustomResponse<List<UserResponseDTO>> getGroupUsers(@PathVariable Long calendarGroupId,
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody PagingRequestDto pagingRequestDto) {
        return inviteService.getGroupUsers(calendarGroupId, userDetails, pagingRequestDto);
    }

    @Operation(summary = "그룹 가입", description = "초대 코드를 사용하여 그룹에 가입합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "그룹 가입 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "422", description = "유효하지 않은 초대 코드입니다."),
        @ApiResponse(responseCode = "409", description = "이미 그룹에 가입되어 있습니다.")
    })
    @PostMapping("/code")
    public CustomResponse<String> joinGroup(@RequestBody String inviteCode,
        @AuthenticationPrincipal UserDetails userDetails) {
        return inviteService.joinGroup(inviteCode, userDetails);
    }

    //다른 유저를 검색하는 시스템 미구현
}