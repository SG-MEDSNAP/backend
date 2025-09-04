package backend.medsnap.domain.medication.entity;

import java.util.List;

public enum DayOfWeek {
    MON, TUE, WED, THU, FRI, SAT, SUN, DAILY;

    public static final List<DayOfWeek> ALL_DAYS = List.of(
            MON, TUE, WED, THU, FRI, SAT, SUN
    );

    public static List<DayOfWeek> expandDays(List<DayOfWeek> requestDays) {
        return requestDays.contains(DAILY) ? ALL_DAYS : requestDays;
    }
}
