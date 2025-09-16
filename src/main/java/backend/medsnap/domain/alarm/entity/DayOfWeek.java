package backend.medsnap.domain.alarm.entity;

import java.util.List;

public enum DayOfWeek {
    MON,
    TUE,
    WED,
    THU,
    FRI,
    SAT,
    SUN,
    DAILY;

    public static final List<DayOfWeek> ALL_DAYS = List.of(MON, TUE, WED, THU, FRI, SAT, SUN);

    public static List<DayOfWeek> expandDays(List<DayOfWeek> requestDays) {
        if (requestDays == null || requestDays.isEmpty()) {
            throw new IllegalArgumentException("Request days cannot be null or empty");
        }
        return requestDays.contains(DAILY) ? List.copyOf(ALL_DAYS) : List.copyOf(requestDays);
    }
}
