package backend.medsnap.domain.medication.dto.request;

import backend.medsnap.domain.medication.entity.DayOfWeek;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "약 등록 요청", example = """
    {
      "name": "타이레놀",
      "imageUrl": "https://example.com/image.jpg",
      "notifyCaregiver": true,
      "preNotify": true,
      "doseTimes": ["09:00","21:00"],
      "doseDays": [
        "MON",
        "TUE",
        "WED"
      ]
    }
    """)
public class MedicationCreateRequest{

    @Schema(description = "약 이름")
    private String name;

    @Schema(description = "약 이미지 URL")
    private String imageUrl;

    @Schema(description = "보호자 알림 여부")
    private Boolean notifyCaregiver;

    @Schema(description = "사전 알림 여부") 
    private Boolean preNotify;

    @Schema(description = "복용 시간 목록")
    @JsonFormat(pattern = "HH:mm")
    private List<LocalTime> doseTimes;

    @Schema(description = "복용 요일 목록")
    private List<DayOfWeek> doseDays;
}
