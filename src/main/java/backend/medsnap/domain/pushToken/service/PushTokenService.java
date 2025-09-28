package backend.medsnap.domain.pushToken.service;

import backend.medsnap.domain.pushToken.dto.request.UpsertPushTokenRequest;
import backend.medsnap.domain.pushToken.entity.Platform;
import backend.medsnap.domain.pushToken.entity.PushToken;
import backend.medsnap.domain.pushToken.exception.PushTokenException;
import backend.medsnap.domain.pushToken.repository.PushTokenRepository;
import backend.medsnap.domain.user.entity.User;
import backend.medsnap.domain.user.exception.UserNotFoundException;
import backend.medsnap.domain.user.repository.UserRepository;
import backend.medsnap.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PushTokenService {

    private final PushTokenRepository pushTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void upsertPushToken(Long userId, UpsertPushTokenRequest request) {

        // 사용자 존재 확인
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(userId));

        Platform platform;
        try {
            platform = Platform.valueOf(request.getPlatform().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new PushTokenException(ErrorCode.PLATFORM_INVALID);
        }

        pushTokenRepository.findByUserAndToken(user, request.getToken()).ifPresentOrElse(
                existingToken -> {
                    if (!existingToken.getIsActive()) {
                        existingToken.reactivate();
                    }
                },
                () -> pushTokenRepository.save(
                        PushToken.builder()
                                .user(user)
                                .token(request.getToken())
                                .platform(platform)
                                .provider("expo")
                                .isActive(true)
                                .build()
                )
        );
    }
}
