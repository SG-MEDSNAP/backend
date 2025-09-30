package backend.medsnap.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.medsnap.domain.user.dto.request.MyPageUpdateRequest;
import backend.medsnap.domain.user.dto.response.MyPageResponse;
import backend.medsnap.domain.user.dto.response.UserInfoResponse;
import backend.medsnap.domain.user.entity.SocialAccount;
import backend.medsnap.domain.user.entity.User;
import backend.medsnap.domain.user.exception.UserNotFoundException;
import backend.medsnap.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Long userId) {

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(userId));

        SocialAccount socialAccount = user.getSocialAccounts().stream().findFirst().orElse(null);

        return UserInfoResponse.builder()
                .id(user.getId())
                .role(user.getRole())
                .name(user.getName())
                .provider(socialAccount != null ? socialAccount.getProvider() : null)
                .birthday(user.getBirthday())
                .phone(user.getPhone())
                .isPushConsent(user.getIsPushConsent())
                .build();
    }

    @Transactional
    public MyPageResponse updateMyPage(Long userId, MyPageUpdateRequest request) {

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(userId));

        user.updateProfile(
                request.getName(),
                request.getBirthday(),
                request.getPhone(),
                // null, // request.getCaregiverPhone() 대신 null
                request.getIsPushConsent());

        return MyPageResponse.from(user);
    }
}
