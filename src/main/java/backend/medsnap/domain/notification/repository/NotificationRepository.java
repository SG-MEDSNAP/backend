package backend.medsnap.domain.notification.repository;

import backend.medsnap.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(value = """
        SELECT * FROM notifications
        WHERE status = 'SCHEDULED' AND (scheduled_at IS NULL OR scheduled_at <= NOW())
        ORDER BY id
        FOR UPDATE SKIP LOCKED
        LIMIT :limit
        """, nativeQuery = true)
    List<Notification> pickDueForDispatch(@Param("limit") int limit);

    @Query("SELECT n " +
            "FROM Notification n " +
            "WHERE n.status = 'SENT' " +
            "AND n.providerMessageId IS NOT NULL " +
            "AND n.updatedAt >= :since")
    List<Notification> findRecentSentWithTickets(@Param("since") java.time.LocalDateTime since);

    @Query("SELECT COUNT(n) > 0 " +
            "FROM Notification n " +
            "WHERE n.user.id = :userId " +
            "AND n.scheduledAt = :scheduledAt " +
            "AND n.title = :title " +
            "AND n.body = :body")
    boolean existsByUserIdAndScheduledAtAndTitleAndBody(@Param("userId") Long userId, @Param("scheduledAt") LocalDateTime scheduledAt, @Param("title") String title, @Param("body") String body);
}
