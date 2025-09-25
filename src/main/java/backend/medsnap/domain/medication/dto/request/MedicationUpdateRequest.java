package backend.medsnap.domain.medication.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import backend.medsnap.domain.alarm.entity.DayOfWeek;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "약 정보 수정 요청",
        example =
                """
    {
      "name": "타이레놀",
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
public class MedicationUpdateRequest {

    @Schema(description = "약 이름")
    @NotBlank(message = "약 이름은 필수입니다")
    private String name;

    @Schema(description = "보호자 알림 여부")
    @NotNull(message = "보호자 알림 여부는 필수입니다")
    private Boolean notifyCaregiver;

    @Schema(description = "사전 알림 여부")
    @NotNull(message = "사전 알림 여부는 필수입니다")
    private Boolean preNotify;

    @Schema(description = "복용 시간 목록 (HH:mm)")
    @NotEmpty(message = "복용 시간은 최소 1개 이상이어야 합니다")
    private List<String> doseTimes;

    @Schema(description = "복용 요일 목록")
    @NotEmpty(message = "복용 요일은 최소 1개 이상이어야 합니다")
    private List<DayOfWeek> doseDays;
}
