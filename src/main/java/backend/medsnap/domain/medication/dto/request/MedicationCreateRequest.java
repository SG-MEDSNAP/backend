package backend.medsnap.domain.medication.dto.request;

import backend.medsnap.domain.medication.entity.DayOfWeek;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class MedicationCreateRequest{

    private String name;
    private String imageUrl;
    private Boolean notifyCaregiver;
    private Boolean preNotify;

    @JsonFormat(pattern = "HH:mm")
    private List<LocalTime> doseTimes;

    private List<DayOfWeek> doseDays;
}
