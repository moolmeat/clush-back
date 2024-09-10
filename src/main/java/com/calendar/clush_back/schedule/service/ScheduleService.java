package com.calendar.clush_back.schedule.service;

import static com.calendar.clush_back.schedule.entity.RecurrenceUpdateType.MODIFY;

import com.calendar.clush_back.common.exception.CustomException;
import com.calendar.clush_back.common.response.CustomResponse;
import com.calendar.clush_back.schedule.dto.GetScheduleDto;
import com.calendar.clush_back.schedule.dto.RecurrenceDto;
import com.calendar.clush_back.schedule.dto.ScheduleDto;
import com.calendar.clush_back.schedule.dto.UserGroupDto;
import com.calendar.clush_back.schedule.entity.CalendarGroup;
import com.calendar.clush_back.schedule.entity.Recurrence;
import com.calendar.clush_back.schedule.entity.RecurrenceType;
import com.calendar.clush_back.schedule.entity.Schedule;
import com.calendar.clush_back.schedule.entity.ScheduleDayOfWeek;
import com.calendar.clush_back.schedule.mapper.ScheduleMapper;
import com.calendar.clush_back.schedule.repository.RecurrenceRepository;
import com.calendar.clush_back.schedule.repository.ScheduleRepository;
import com.calendar.clush_back.schedule.validator.UserGroupValidator;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
                case DAILY:
                    handleDailySchedules(result, schedule, fromDate, toDate, exceptions);
                    break;
                case WEEKLY:
                    handleWeeklySchedules(result, schedule, fromDate, toDate, exceptions);
                    break;
                case MONTHLY:
                    handleMonthlySchedules(result, schedule, fromDate, toDate, exceptions);
                    break;
                case YEARLY:
                    handleYearlySchedules(result, schedule, fromDate, toDate, exceptions);
                    break;
                default:
                    throw new CustomException("잘못된 반복 규칙입니다.", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        return result;
    }

    // DAILY 반복 일정 추가
    private void handleDailySchedules(List<ScheduleDto> result, Schedule schedule,
        LocalDate fromDate,
        LocalDate toDate, List<Recurrence> exceptions) {
        LocalDate date = fromDate;
        while (!date.isAfter(toDate)) {
            processScheduleWithExceptions(result, schedule, date, date, exceptions);
            date = date.plusDays(1);
        }

    }

    // WEEKLY 반복 일정 추가
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

    // MONTHLY 반복 일정 추가
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

    // YEARLY 반복 일정 추가
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

    // 반복 일정 예외 검사
    private void processScheduleWithExceptions(List<ScheduleDto> result, Schedule schedule,
        LocalDate startDate, LocalDate endDate, List<Recurrence> exceptions) {

        Optional<Recurrence> exception = getExceptionForDate(exceptions, startDate);
        LocalTime startTime = schedule.getStartTime();
        LocalTime endTime = schedule.getEndTime();

        if (exception.isPresent()) {
            Recurrence recurrence = exception.get();
            if (recurrence.getUpdateType() == MODIFY) {
                result.add(scheduleMapper.toDto(schedule, recurrence, startDate, startTime, endDate,
                    endTime));
            }
            // "EXCLUDE"인 경우 추가하지 않음
        } else {
            result.add(scheduleMapper.toDto(schedule, startDate, startTime, endDate, endTime));
        }
    }

    // 예외 일자 검사
    private Optional<Recurrence> getExceptionForDate(List<Recurrence> exceptions, LocalDate date) {
        return exceptions.stream()
            .filter(recurrence -> recurrence.getExceptionDate().equals(date))
            .findFirst();
    }

    private boolean matchesRecurrenceRule(Schedule schedule, LocalDate date) {
        if (RecurrenceType.WEEKLY.equals(schedule.getRecurrenceType())
            && schedule.getDaysOfWeek() != null) {
            List<ScheduleDayOfWeek> daysOfWeek = schedule.getDaysOfWeek();
            // 현재 날짜의 DayOfWeek를 문자열로 변환하여 비교
            String currentDay = date.getDayOfWeek().name().substring(0, 2).toUpperCase();

            // ScheduleDayOfWeek의 value와 비교
            return daysOfWeek.stream().anyMatch(day -> day.getValue().equals(currentDay));
        }
        return true;
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

    // 반복 일정 중 예외 추가 또는 수정
    public CustomResponse<Void> updateRecurrence(Long scheduleId, RecurrenceDto recurrenceDto,
        UserDetails userDetails) {

        // 스케줄 유효성 검사
        Schedule schedule = userGroupValidator.checkScheduleValidity(userDetails, scheduleId);

        // 해당 스케줄과 예외 날짜에 해당하는 Recurrence가 이미 존재하는지 확인
        Optional<Recurrence> existingRecurrence = recurrenceRepository.findByScheduleAndExceptionDate(
            schedule, recurrenceDto.getExceptionDate());

        Recurrence recurrence = existingRecurrence.orElseGet(() -> new Recurrence(
            recurrenceDto.getExceptionDate(),
            recurrenceDto.getUpdateType(),
            schedule)
        );

        recurrence.setUpdateType(recurrenceDto.getUpdateType());
        recurrence.setModifiedTitle(recurrenceDto.getModifiedTitle());
        recurrence.setModifiedContent(recurrenceDto.getModifiedContent());

        recurrenceRepository.save(recurrence);

        String message = existingRecurrence.isPresent() ? "예외 처리 수정 성공" : "예외 처리 추가 성공";
        return CustomResponse.success(message, null, existingRecurrence.isPresent() ? 200 : 201);
    }

}
