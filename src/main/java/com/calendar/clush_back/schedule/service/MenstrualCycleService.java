package com.calendar.clush_back.schedule.service;

import static java.time.temporal.ChronoUnit.DAYS;

import com.calendar.clush_back.common.exception.CustomException;
import com.calendar.clush_back.common.response.CustomResponse;
import com.calendar.clush_back.schedule.dto.GetScheduleDto;
import com.calendar.clush_back.schedule.dto.ScheduleStatusDto;
import com.calendar.clush_back.schedule.dto.UserGroupDto;
import com.calendar.clush_back.schedule.entity.MenstrualCycle;
import com.calendar.clush_back.schedule.repository.CalendarGroupRepository;
import com.calendar.clush_back.schedule.repository.MenstrualCycleRepository;
import com.calendar.clush_back.schedule.validator.UserGroupValidator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class MenstrualCycleService {

    private final MenstrualCycleRepository menstrualCycleRepository;
    private final UserGroupValidator userGroupValidator;

    public MenstrualCycleService(MenstrualCycleRepository menstrualCycleRepository,
        UserGroupValidator userGroupValidator) {
        this.menstrualCycleRepository = menstrualCycleRepository;
        this.userGroupValidator = userGroupValidator;
    }

    // 생리 시작일 저장 또는 업데이트
    public CustomResponse<String> setMenstrualStartDate(Long calendarGroupId, LocalDate startDate,
        UserDetails userDetails) {

        UserGroupDto userGroupDto = userGroupValidator.checkMemberValidity(userDetails,
            calendarGroupId);

        MenstrualCycle existingCycle = menstrualCycleRepository.findByCalendarGroup(
                userGroupDto.getCalendarGroup())
            .orElse(null);

        if (existingCycle != null) {
            existingCycle.setStartDate(startDate);
            menstrualCycleRepository.save(existingCycle);
            return CustomResponse.success("생리 시작일이 성공적으로 수정되었습니다.", null, 200);
        } else {
            MenstrualCycle menstrualCycle = new MenstrualCycle();
            menstrualCycle.setStartDate(startDate);
            menstrualCycle.setCalendarGroup(userGroupDto.getCalendarGroup());
            menstrualCycleRepository.save(menstrualCycle);
            return CustomResponse.success("생리 시작일이 성공적으로 저장되었습니다.", null, 201);
        }
    }


    // 생리 주기 상태 조회
    public CustomResponse<List<ScheduleStatusDto>> getMenstrualStatuses(Long calendarGroupId,
        GetScheduleDto getScheduleDto, UserDetails userDetails) {

        UserGroupDto userGroupDto = userGroupValidator.checkMemberValidity(userDetails,
            calendarGroupId);

        MenstrualCycle menstrualCycle = menstrualCycleRepository.findByCalendarGroup(
                userGroupDto.getCalendarGroup())
            .orElseThrow(() -> new CustomException("생리 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        List<ScheduleStatusDto> statuses = calculateStatusesForPeriod(menstrualCycle.getStartDate(),
            getScheduleDto.getFromDate(), getScheduleDto.getToDate());

        return CustomResponse.success("생리 상태 조회 성공", statuses, 200);
    }

    // 특정 기간 동안의 상태를 계산
    private List<ScheduleStatusDto> calculateStatusesForPeriod(LocalDate cycleStartDate,
        LocalDate fromDate, LocalDate toDate) {
        List<ScheduleStatusDto> statuses = new ArrayList<>();

        // 생리 주기 기본 설정 (28일 주기, 5일 생리 기간)
        int cycleLength = 28;
        int periodLength = 5;
        int ovulationDay = 14; // 배란일은 생리 시작일 기준 14일 후

        LocalDate currentDate = fromDate;
        while (!currentDate.isAfter(toDate)) {
            long daysSinceCycleStart = DAYS.between(cycleStartDate, currentDate) % cycleLength;

            String status;
            if (daysSinceCycleStart < periodLength) {
                status = "생리 중";
            } else if (daysSinceCycleStart == ovulationDay) {
                status = "배란일";
            } else if (daysSinceCycleStart > ovulationDay - 5
                && daysSinceCycleStart < ovulationDay) {
                status = "가임기";
            } else {
                status = "준비일";
            }

            ScheduleStatusDto scheduleStatus = new ScheduleStatusDto();
            scheduleStatus.setDate(currentDate);
            scheduleStatus.setStatus(status);
            statuses.add(scheduleStatus);

            currentDate = currentDate.plusDays(1);
        }

        return statuses;
    }
}