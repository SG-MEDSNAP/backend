package backend.medsnap.domain.notification.entity;

import backend.medsnap.domain.user.entity.User;
import backend.medsnap.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications", 
       uniqueConstraints = {
           @UniqueConstraint(
               name = "ux_notifications_dedupe",
               columnNames = {"user_id", "scheduled_at", "title", "body"}
           )
       })
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> data;

    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private NotificationStatus status;

    private String providerMessageId;

    private String errorCode;

    private Notification(User user, String title, String body,
                         Map<String, Object> data, LocalDateTime scheduledAt) {
        this.user = user;
        this.title = title;
        this.body = body;
        this.data = data;
        this.scheduledAt = scheduledAt;
        this.status = NotificationStatus.SCHEDULED;
    }

    public static Notification create(User user, String title, String body,
                                      Map<String, Object> data, LocalDateTime scheduledAt) {
        return new Notification(user, title, body, data, scheduledAt);
    }

    public void markSent(String messageIds) {
        this.status = NotificationStatus.SENT;
        this.providerMessageId = messageIds;
    }

    public void markDelivered() {
        this.status = NotificationStatus.DELIVERED;
    }

    public void markProviderError(String errorCode) {
        this.status = NotificationStatus.PROVIDER_ERROR;
        this.errorCode = errorCode;
    }

    public void cancel() {
        this.status = NotificationStatus.CANCELED;
    }
}
