package backend.medsnap.domain.alarm.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmDeleteRequest {

    @NotNull(message = "삭제할 알람 ID 목록은 필수입니다.")
    @NotEmpty(message = "삭제할 알람 ID 목록은 비어있을 수 없습니다.")
    private List<Long> alarmIds;
}
