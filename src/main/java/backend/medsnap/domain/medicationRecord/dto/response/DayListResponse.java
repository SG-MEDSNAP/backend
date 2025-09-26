package backend.medsnap.domain.medicationRecord.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import backend.medsnap.domain.medicationRecord.entity.MedicationRecordStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayListResponse {

    private LocalDate date;
    private List<Item> items;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class Item {

        private Long recordId;

        @JsonFormat(pattern = "HH:mm")
        private LocalTime alarmTime;

        private Long medicationId;
        private String medicationName;
        private MedicationRecordStatus status;

        // 복약 상세 화면 데이터
        private String imageUrl;
        private LocalDateTime checkedAt;
        private LocalDateTime firstAlarmAt;
        private LocalDateTime secondAlarmAt;
        private LocalDateTime caregiverNotifiedAt;
    }
}
