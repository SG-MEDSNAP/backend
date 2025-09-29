package backend.medsnap.domain.notification.service;

import backend.medsnap.domain.notification.dto.request.NotificationCreateRequest;
import backend.medsnap.domain.notification.entity.Notification;
import backend.medsnap.domain.notification.repository.NotificationRepository;
import backend.medsnap.domain.user.entity.User;
import backend.medsnap.domain.user.exception.UserNotFoundException;
import backend.medsnap.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createNotification(Long userId, NotificationCreateRequest request) {

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(userId));

        OffsetDateTime scheduledAt = request.getScheduledAt() != null
                ? request.getScheduledAt().atOffset(ZoneOffset.UTC)
                : null;

        Notification notification = Notification.create(
                user,
                request.getTitle(),
                request.getBody(),
                request.getData(),
                scheduledAt
        );

        return notificationRepository.save(notification).getId();
    }
}
