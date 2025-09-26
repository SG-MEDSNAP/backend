package backend.medsnap.domain.medicationRecord.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

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
    public static class Item {
        @JsonFormat(pattern = "HH:mm")
        private LocalTime alarmTime;

        private Long medicationId;
        private String medicationName;
        private MedicationRecordStatus status;
    }
}
