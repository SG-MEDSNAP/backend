package backend.medsnap.domain.notification.service;

import backend.medsnap.domain.notification.dto.request.NotificationCreateRequest;
import backend.medsnap.domain.notification.entity.Notification;
import backend.medsnap.domain.notification.repository.NotificationRepository;
import backend.medsnap.domain.user.entity.User;
import backend.medsnap.domain.user.exception.UserNotFoundException;
import backend.medsnap.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
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

        LocalDateTime scheduledAt = request.getScheduledAt();

        Notification notification = Notification.create(
                user,
                request.getTitle(),
                request.getBody(),
                request.getData(),
                scheduledAt
        );

        try {
            return notificationRepository.save(notification).getId();
        } catch (DataIntegrityViolationException e) {
            log.warn("중복 알림으로 저장 생략: userId={}, scheduledAt={}, title={}, body={}",
                    userId, scheduledAt, request.getTitle(), request.getBody());
            return null; // 중복으로 인한 생성 실패
        }
    }

}
