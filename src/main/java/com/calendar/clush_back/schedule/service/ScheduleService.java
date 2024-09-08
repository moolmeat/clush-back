package com.calendar.clush_back.schedule.service;

import com.calendar.clush_back.auth.entity.User;
import com.calendar.clush_back.auth.repository.UserRepository;
import com.calendar.clush_back.common.exception.CustomException;
import com.calendar.clush_back.common.response.CustomResponse;
import com.calendar.clush_back.schedule.dto.GetScheduleDto;
import com.calendar.clush_back.schedule.dto.RecurrenceDto;
import com.calendar.clush_back.schedule.dto.ScheduleDto;
import com.calendar.clush_back.schedule.dto.UserGroupDto;
import com.calendar.clush_back.schedule.entity.CalendarGroup;
import com.calendar.clush_back.schedule.entity.Recurrence;
import com.calendar.clush_back.schedule.entity.Schedule;
import com.calendar.clush_back.schedule.mapper.ScheduleMapper;
import com.calendar.clush_back.schedule.repository.CalendarGroupRepository;
import com.calendar.clush_back.schedule.repository.RecurrenceRepository;
import com.calendar.clush_back.schedule.repository.ScheduleRepository;
import com.calendar.clush_back.schedule.validator.UserGroupValidator;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final RecurrenceRepository recurrenceRepository;
    private final ScheduleMapper scheduleMapper;
    private final UserGroupValidator userGroupValidator;

    public ScheduleService(ScheduleRepository scheduleRepository,
        RecurrenceRepository recurrenceRepository,
        ScheduleMapper scheduleMapper,
        UserGroupValidator userGroupValidator) {
        this.scheduleRepository = scheduleRepository;
        this.recurrenceRepository = recurrenceRepository;
        this.scheduleMapper = scheduleMapper;
        this.userGroupValidator = userGroupValidator;
    }

    // 그룹 내에서 스케줄 생성
    @Transactional
    public CustomResponse<ScheduleDto> createScheduleInGroup(Long calendarGroupId,
        ScheduleDto scheduleDto, UserDetails userDetails) {

        UserGroupDto userGroupDto = userGroupValidator.checkMemberValidity(userDetails,
            calendarGroupId);

        Schedule savedSchedule = scheduleRepository.save(
            scheduleMapper.toEntity(scheduleDto, userGroupDto.getCalendarGroup()));

        return CustomResponse.success("스케줄 생성 성공", scheduleMapper.toDto(savedSchedule),
            201);
    }

    // 기간 내 모든 일정 조회
    public CustomResponse<List<ScheduleDto>> getGroupSchedules(Long calendarGroupId,
        GetScheduleDto getScheduleDto, UserDetails userDetails) {

        UserGroupDto userGroupDto = userGroupValidator.checkMemberValidity(userDetails,
            calendarGroupId);

        List<ScheduleDto> result = new ArrayList<>();

        // 단일 일정 추가
        List<Schedule> schedules = scheduleRepository.findAllByGroupAndDateRange(
            userGroupDto.getCalendarGroup(),
            getScheduleDto.getFromDate(), getScheduleDto.getToDate());

        // 반복 일정 추가
        List<ScheduleDto> recurrenceSchedules = generateRecurringSchedulesInRange(
            userGroupDto.getCalendarGroup(),
            getScheduleDto.getFromDate(), getScheduleDto.getToDate());

        // 일반 일정 처리
        for (Schedule schedule : schedules) {
            result.add(scheduleMapper.toDto(schedule));
        }

        // 반복 일정 처리
        result.addAll(recurrenceSchedules);

//        시간 순 정렬은 프런트 UI/UX에 맞춰 처리할 수 있음
//        result.sort(Comparator
//            .comparing(ScheduleDto::getStartDate)
//            .thenComparing(ScheduleDto::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));

        return CustomResponse.success("스케줄 조회 성공", result, 200);
    }

    // 특정 기간 내에서 반복 일정을 동적으로 생성하여 반환 (예외 처리 포함)
    private List<ScheduleDto> generateRecurringSchedulesInRange(CalendarGroup calendarGroup,
        LocalDate fromDate, LocalDate toDate) {

        List<ScheduleDto> result = new ArrayList<>();

        // 반복 규칙이 있는 스케줄만 가져오기
        List<Schedule> schedules = scheduleRepository.findSchedulesWithRecurrenceByGroup(
            calendarGroup);

        for (Schedule schedule : schedules) {
            List<Recurrence> exceptions = recurrenceRepository.findAllBySchedule(schedule);

            switch (schedule.getRecurrenceType()) {
                case "DAILY":
                    handleDailySchedules(result, schedule, fromDate, toDate, exceptions);
                    break;
                case "WEEKLY":
                    handleWeeklySchedules(result, schedule, fromDate, toDate, exceptions);
                    break;
                case "MONTHLY":
                    handleMonthlySchedules(result, schedule, fromDate, toDate, exceptions);
                    break;
                case "YEARLY":
                    handleYearlySchedules(result, schedule, fromDate, toDate, exceptions);
                    break;
                default:
                    throw new CustomException("잘못된 반복 규칙입니다.", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        return result;
    }

    private void handleDailySchedules(List<ScheduleDto> result, Schedule schedule,
        LocalDate fromDate,
        LocalDate toDate, List<Recurrence> exceptions) {
        LocalDate date = fromDate;
        while (!date.isAfter(toDate)) {
            processScheduleWithExceptions(result, schedule, date, date, exceptions);
            date = date.plusDays(1);
        }

    }

    private void handleWeeklySchedules(List<ScheduleDto> result, Schedule schedule,
        LocalDate fromDate,
        LocalDate toDate, List<Recurrence> exceptions) {
        LocalDate date = fromDate;
        while (!date.isAfter(toDate)) {
            if (matchesRecurrenceRule(schedule, date)) {
                processScheduleWithExceptions(result, schedule, date, date, exceptions);
            }
            date = date.plusDays(1);
        }
    }

    private void handleMonthlySchedules(List<ScheduleDto> result, Schedule schedule,
        LocalDate fromDate,
        LocalDate toDate, List<Recurrence> exceptions) {
        LocalDate date = fromDate.withDayOfMonth(1);
        while (!date.isAfter(toDate)) {
            LocalDate targetDate = date.withDayOfMonth(schedule.getStartDate().getDayOfMonth());
            if (!targetDate.isBefore(fromDate) && !targetDate.isAfter(toDate)) {
                processScheduleWithExceptions(result, schedule, targetDate, targetDate, exceptions);
            }
            date = date.plusMonths(1);
        }
    }

    private void handleYearlySchedules(List<ScheduleDto> result, Schedule schedule,
        LocalDate fromDate,
        LocalDate toDate, List<Recurrence> exceptions) {
        LocalDate date = fromDate.withDayOfYear(1);
        while (!date.isAfter(toDate)) {
            LocalDate targetDate = date.withDayOfMonth(schedule.getStartDate().getDayOfMonth())
                .withMonth(schedule.getStartDate().getMonthValue());
            if (!targetDate.isBefore(fromDate) && !targetDate.isAfter(toDate)) {
                processScheduleWithExceptions(result, schedule, targetDate, targetDate, exceptions);
            }
            date = date.plusYears(1);
        }
    }

    private void processScheduleWithExceptions(List<ScheduleDto> result, Schedule schedule,
        LocalDate startDate, LocalDate endDate, List<Recurrence> exceptions) {

        Optional<Recurrence> exception = getExceptionForDate(exceptions, startDate);
        LocalTime startTime = schedule.getStartTime();
        LocalTime endTime = schedule.getEndTime();

        if (exception.isPresent()) {
            Recurrence recurrence = exception.get();
            if ("MODIFY".equals(recurrence.getExceptionType())) {
                result.add(scheduleMapper.toDto(schedule, recurrence, startDate, startTime, endDate,
                    endTime));
            }
            // "EXCLUDE"인 경우 추가하지 않음
        } else {
            result.add(scheduleMapper.toDto(schedule, startDate, startTime, endDate, endTime));
        }
    }

    private Optional<Recurrence> getExceptionForDate(List<Recurrence> exceptions, LocalDate date) {
        return exceptions.stream()
            .filter(recurrence -> recurrence.getExceptionDate().equals(date))
            .findFirst();
    }

    // 반복 규칙에 맞는 요일 확인
    private boolean matchesRecurrenceRule(Schedule schedule, LocalDate date) {
        if ("WEEKLY".equals(schedule.getRecurrenceType()) && schedule.getDaysOfWeek() != null) {
            String[] daysOfWeek = schedule.getDaysOfWeek().split(",");
            String dayOfWeek = date.getDayOfWeek().name().substring(0, 2).toUpperCase();
            return Arrays.asList(daysOfWeek).contains(dayOfWeek);
        }
        return true; // 다른 규칙이 없는 경우 기본적으로 일정을 추가
    }

    // 스케줄 수정
    public CustomResponse<Void> updateSchedule(Long scheduleId, ScheduleDto scheduleDto,
        UserDetails userDetails) {

        Schedule schedule = userGroupValidator.checkScheduleValidity(userDetails, scheduleId);

        schedule.setTitle(scheduleDto.getTitle());
        schedule.setContent(scheduleDto.getContent());
        schedule.setStartDate(scheduleDto.getStartDate());
        schedule.setEndDate(scheduleDto.getEndDate());
        schedule.setStartTime(scheduleDto.getStartTime());
        schedule.setEndTime(scheduleDto.getEndTime());
        schedule.setRecurrenceType(scheduleDto.getRecurrenceType());
        schedule.setDaysOfWeek(scheduleDto.getDaysOfWeek());

        scheduleRepository.save(schedule);

        return CustomResponse.success("스케줄 수정 성공", null,
            204);
    }

    // 스케줄 삭제
    public CustomResponse<Void> deleteSchedule(Long scheduleId, UserDetails userDetails) {

        Schedule schedule = userGroupValidator.checkScheduleValidity(userDetails, scheduleId);

        scheduleRepository.delete(schedule);
        return CustomResponse.success("스케줄 삭제 성공", null, HttpStatus.NO_CONTENT.value());
    }

    // 반복 일정 중 예외 추가
    public CustomResponse<Void> addRecurrence(Long scheduleId, RecurrenceDto recurrenceDto,
        UserDetails userDetails) {

        Schedule schedule = userGroupValidator.checkScheduleValidity(userDetails, scheduleId);

        // Recurrence 생성자가 LocalDate를 받으므로 그대로 사용
        Recurrence exception = new Recurrence(recurrenceDto.getExceptionDate(),
            recurrenceDto.getExceptionType(),
            schedule);

        if ("MODIFY".equals(recurrenceDto.getExceptionType())) {
            exception.setModifiedTitle(recurrenceDto.getModifiedTitle());
            exception.setModifiedContent(recurrenceDto.getModifiedContent());
        }

        recurrenceRepository.save(exception);

        return CustomResponse.success("예외 처리 추가 성공", null, 201);
    }
}
