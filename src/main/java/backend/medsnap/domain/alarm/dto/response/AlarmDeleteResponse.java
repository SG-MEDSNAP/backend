package backend.medsnap.domain.alarm.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AlarmDeleteResponse {

    private Long medicationId;
    private String medicationName;
    private Integer deletedAlarmCount;
    private Integer failedAlarmCount;
    private String message;
    private List<AlarmDeleteDetail> details;

    @Getter
    @Builder
    public static class AlarmDeleteDetail {
        private Long alarmId;
        private DeleteStatus status;
    }

    public enum DeleteStatus {
        DELETED("삭제됨"),
        NOT_FOUND("알람을 찾을 수 없음"),
        UNAUTHORIZED("권한 없음");

        private final String description;

        DeleteStatus(String description) {
            this.description = description;
        }
    }
}
