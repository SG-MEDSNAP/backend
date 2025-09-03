package backend.medsnap.domain.medication.dto.response;

import backend.medsnap.domain.medication.entity.DayOfWeek;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class MedicationResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private Boolean notifyCaregiver;
    private Boolean preNotify;

    @JsonFormat(pattern = "HH:mm")
    private List<LocalTime> doseTimes;

    private List<DayOfWeek> doseDays;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
