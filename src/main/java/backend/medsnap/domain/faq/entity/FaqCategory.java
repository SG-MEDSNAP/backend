package backend.medsnap.domain.faq.entity;

public enum FaqCategory {
    MEDICATION_STATUS("복약 현황"),
    NOTIFICATION("알림"),
    TIMELINE("타임라인");

    private final String description;

    FaqCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
