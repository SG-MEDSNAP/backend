package backend.medsnap.domain.medicationRecord.controller;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend.medsnap.domain.auth.dto.token.CustomUserDetails;
import backend.medsnap.domain.medicationRecord.dto.response.DayListResponse;
import backend.medsnap.domain.medicationRecord.service.MedicationRecordService;
import backend.medsnap.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/medication-records")
@RequiredArgsConstructor
public class MedicationRecordController implements MedicationRecordSwagger {

    private final MedicationRecordService medicationRecordService;

    /**
     * 달력 점 표시용 날짜 목록 조회
     */
    @Override
    @GetMapping("/dates")
    public ResponseEntity<ApiResponse<Set<LocalDate>>> getCalendarDots(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam int year,
            @RequestParam int month) {

        Set<LocalDate> datesWithRecords = medicationRecordService.getDatesWithRecordsByMonth(user.getId(), year, month);
        return ResponseEntity.ok(ApiResponse.success(datesWithRecords));
    }

    /** 특정 날짜의 복약 목록 조회 */
    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<DayListResponse>> getDayList(
            @AuthenticationPrincipal CustomUserDetails user, @RequestParam LocalDate date) {
        DayListResponse response = medicationRecordService.getDayList(user.getId(), date);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
