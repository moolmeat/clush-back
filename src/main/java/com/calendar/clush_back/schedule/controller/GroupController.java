package com.calendar.clush_back.schedule.controller;

import com.calendar.clush_back.common.request.PagingRequestDto;
import com.calendar.clush_back.common.response.CustomResponse;
import com.calendar.clush_back.schedule.dto.CalendarGroupDto;
import com.calendar.clush_back.schedule.service.CalendarGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "그룹 관리", description = "그룹 생성, 수정, 삭제 및 조회 기능을 제공합니다.")
@RestController
@RequestMapping("/groups")
public class GroupController {

    private final CalendarGroupService calendarGroupService;

    public GroupController(CalendarGroupService calendarGroupService) {
        this.calendarGroupService = calendarGroupService;
    }

    @Operation(summary = "그룹 생성", description = "새로운 그룹을 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "그룹 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),
    })
    @PostMapping
    public CustomResponse<CalendarGroupDto> createGroup(@RequestBody String name,
        @AuthenticationPrincipal UserDetails userDetails) {
        return calendarGroupService.createGroup(name, userDetails);
    }

    @Operation(summary = "사용자의 그룹 조회", description = "로그인된 사용자가 속한 모든 그룹을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "그룹 조회 성공"),
        @ApiResponse(responseCode = "204", description = "사용자가 속한 그룹 없음"),
        @ApiResponse(responseCode = "403", description = "접근이 거부됨"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),
    })
    @GetMapping
    public CustomResponse<List<CalendarGroupDto>> getUserGroups(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody PagingRequestDto pagingRequestDto
    ) {
        return calendarGroupService.getUserGroups(userDetails, pagingRequestDto);
    }

    @Operation(summary = "그룹 이름 수정", description = "특정 그룹의 이름을 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "그룹 이름 수정 성공"),
        @ApiResponse(responseCode = "403", description = "접근이 거부됨"),
        @ApiResponse(responseCode = "404", description = "그룹이 존재하지 않음"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),
    })
    @PutMapping("/{calendarGroupId}/name")
    public CustomResponse<Void> updateGroupName(@PathVariable Long calendarGroupId,
        @RequestBody String newName, @AuthenticationPrincipal UserDetails userDetails) {
        return calendarGroupService.updateGroupName(calendarGroupId, newName, userDetails);
    }

    @Operation(summary = "그룹 삭제", description = "특정 그룹을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "그룹 삭제 성공"),
        @ApiResponse(responseCode = "403", description = "접근이 거부됨"),
        @ApiResponse(responseCode = "404", description = "그룹이 존재하지 않음"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),
    })
    @DeleteMapping("/{calendarGroupId}")
    public CustomResponse<Void> deleteGroup(@PathVariable Long calendarGroupId,
        @AuthenticationPrincipal UserDetails userDetails) {
        return calendarGroupService.deleteGroup(calendarGroupId, userDetails);
    }
}
