package backend.medsnap.domain.pushToken.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
@RequiredArgsConstructor
public class PushTokenService {

    private final PushTokenRepository pushTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public PushToken upsertPushToken(Long userId, UpsertPushTokenRequest request) {

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

        return pushTokenRepository
                .findByUser(user)
                .map(
                        existingToken -> {
                            // 기존 푸시토큰이 있으면 토큰, 플랫폼, 활성화 상태 업데이트
                            existingToken.updateToken(request.getToken());
                            existingToken.updatePlatform(platform);
                            if (!existingToken.getIsActive()) {
                                existingToken.reactivate();
                            }
                            return pushTokenRepository.save(existingToken);
                        })
                .orElseGet(
                        () ->
                                pushTokenRepository.save(
                                        PushToken.builder()
                                                .user(user)
                                                .token(request.getToken())
                                                .platform(platform)
                                                .provider("expo")
                                                .isActive(true)
                                                .build()));
    }
}
