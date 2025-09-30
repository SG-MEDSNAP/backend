package backend.medsnap.domain.medication.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import backend.medsnap.domain.alarm.entity.DayOfWeek;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(
        description = "약 정보 응답",
        example =
                """
    {
      "id": 1,
      "name": "타이레놀",
      "imageUrl": "https://example.com/image.jpg",
      "preNotify": true,
      "doseTimes": ["09:00","21:00"],
      "doseDays": [
        "MON",
        "TUE",
        "WED"
      ],
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    }
    """)
public class MedicationResponse {

    @Schema(description = "약 ID")
    private Long id;

    @Schema(description = "약 이름")
    private String name;

    @Schema(description = "약 이미지 URL")
    private String imageUrl;

    // @Schema(description = "보호자 알림 여부")
    // private Boolean notifyCaregiver;

    @Schema(description = "사전 알림 여부")
    private Boolean preNotify;

    @Schema(description = "복용 시간 목록")
    @JsonFormat(pattern = "HH:mm")
    private List<LocalTime> doseTimes;

    @Schema(description = "복용 요일 목록")
    private List<DayOfWeek> doseDays;

    @Schema(description = "생성 시간")
    private final LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    private final LocalDateTime updatedAt;
}
