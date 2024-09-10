package com.calendar.clush_back.schedule.controller;

import com.calendar.clush_back.common.response.CustomResponse;
import com.calendar.clush_back.schedule.dto.GetScheduleDto;
import com.calendar.clush_back.schedule.dto.RecurrenceDto;
import com.calendar.clush_back.schedule.dto.ScheduleDto;
import com.calendar.clush_back.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "일정 관리", description = "일정 생성, 조회, 수정, 삭제 기능을 제공합니다.")
@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Operation(summary = "그룹 내 일정 생성", description = "특정 그룹 내에서 일정을 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "일정 생성 성공"),
        @ApiResponse(responseCode = "403", description = "접근이 거부됨"),
        @ApiResponse(responseCode = "404", description = "그룹이 존재하지 않음"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),
    })
    @PostMapping("/{calendarGroupId}")
    public CustomResponse<ScheduleDto> createScheduleInGroup(@PathVariable Long calendarGroupId,
        @RequestBody ScheduleDto scheduleDto, @AuthenticationPrincipal UserDetails userDetails) {
        return scheduleService.createScheduleInGroup(calendarGroupId, scheduleDto, userDetails);
    }

    @Operation(summary = "특정 기간의 일정 조회", description = "특정 그룹에서 기간 내 모든 일정을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "일정 조회 성공"),
        @ApiResponse(responseCode = "403", description = "접근이 거부됨"),
        @ApiResponse(responseCode = "404", description = "그룹이 존재하지 않음"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),

    })
    @GetMapping("/{calendarGroupId}")
    public CustomResponse<List<ScheduleDto>> getGroupSchedules(@PathVariable Long calendarGroupId,
        @RequestBody GetScheduleDto getScheduleDto,
        @AuthenticationPrincipal UserDetails userDetails) {
        return scheduleService.getGroupSchedules(calendarGroupId, getScheduleDto, userDetails);
    }

    @Operation(summary = "일정 수정", description = "특정 일정을 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "일정 수정 성공"),
        @ApiResponse(responseCode = "403", description = "접근이 거부됨"),
        @ApiResponse(responseCode = "404", description = "일정이 존재하지 않음"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),
    })
    @PutMapping("/{scheduleId}")
    public CustomResponse<Void> updateSchedule(@PathVariable Long scheduleId,
        @RequestBody ScheduleDto scheduleDto, @AuthenticationPrincipal UserDetails userDetails) {
        return scheduleService.updateSchedule(scheduleId, scheduleDto, userDetails);
    }

    @Operation(summary = "일정 삭제", description = "특정 일정을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "일정 삭제 성공"),
        @ApiResponse(responseCode = "403", description = "접근이 거부됨"),
        @ApiResponse(responseCode = "404", description = "일정이 존재하지 않음"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),
    })
    @DeleteMapping("/{scheduleId}")
    public CustomResponse<Void> deleteSchedule(@PathVariable Long scheduleId,
        @AuthenticationPrincipal UserDetails userDetails) {
        return scheduleService.deleteSchedule(scheduleId, userDetails);
    }

    @Operation(summary = "반복 일정 예외 추가", description = "특정 일정에 반복 예외를 추가합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "반복 일정 예외 수정 성공"),
        @ApiResponse(responseCode = "201", description = "반복 일정 예외 추가 성공"),
        @ApiResponse(responseCode = "403", description = "접근이 거부됨"),
        @ApiResponse(responseCode = "404", description = "일정이 존재하지 않음"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),
    })
    @PostMapping("/{scheduleId}/exceptions")
    public CustomResponse<Void> addRecurrence(@PathVariable Long scheduleId,
        @RequestBody RecurrenceDto recurrenceDto,
        @AuthenticationPrincipal UserDetails userDetails) {
        return scheduleService.updateRecurrence(scheduleId, recurrenceDto, userDetails);
    }
}