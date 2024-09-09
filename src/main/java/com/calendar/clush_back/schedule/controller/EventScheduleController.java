package com.calendar.clush_back.schedule.controller;

import com.calendar.clush_back.common.response.CustomResponse;
import com.calendar.clush_back.schedule.dto.GetScheduleDto;
import com.calendar.clush_back.schedule.dto.ScheduleStatusDto;
import com.calendar.clush_back.schedule.service.MenstrualCycleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공통 이벤트 스케쥴 관리", description = "공통 스케쥴 관리 기능을 제공합니다.")
@RestController
@RequestMapping("/menstrual")
public class EventScheduleController {

    @Autowired
    private MenstrualCycleService menstrualCycleService;

    @Operation(summary = "생리 시작일 설정", description = "사용자가 생리 시작일을 설정하고 정보를 저장합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "생리 시작일 수정 성공"),
        @ApiResponse(responseCode = "201", description = "생리 시작일 저장 성공"),
        @ApiResponse(responseCode = "403", description = "접근이 거부됨"),
        @ApiResponse(responseCode = "404", description = "그룹이 존재하지 않음"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),
    })
    @PostMapping("/start/{calendarGroupId}")
    public CustomResponse<String> setMenstrualStartDate(
        @PathVariable Long calendarGroupId,
        @RequestBody LocalDate startDate,
        @AuthenticationPrincipal UserDetails userDetails) {

        System.out.println(startDate);

        return menstrualCycleService.setMenstrualStartDate(calendarGroupId, startDate, userDetails);
    }

    @Operation(summary = "기간 내 생리 상태 조회", description = "특정 기간 동안의 생리 상태를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "생리 상태 조회 성공"),
        @ApiResponse(responseCode = "403", description = "접근이 거부됨"),
        @ApiResponse(responseCode = "404", description = "그룹이 존재하지 않음"),
        @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음"),
        @ApiResponse(responseCode = "404", description = "생리 정보 없음"),
    })
    @GetMapping("/status/{calendarGroupId}")
    public CustomResponse<List<ScheduleStatusDto>> getMenstrualCycleStatuses(
        @PathVariable Long calendarGroupId,
        @RequestBody GetScheduleDto getScheduleDto,
        @AuthenticationPrincipal UserDetails userDetails) {

        return menstrualCycleService.getMenstrualStatuses(calendarGroupId, getScheduleDto, userDetails);
    }
}
