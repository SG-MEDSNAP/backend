package backend.medsnap.domain.user.service;

import backend.medsnap.domain.user.dto.request.MyPageUpdateRequest;
import backend.medsnap.domain.user.dto.response.MyPageResponse;
import backend.medsnap.domain.user.entity.User;
import backend.medsnap.domain.user.exception.UserNotFoundException;
import backend.medsnap.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public MyPageResponse updateMyPage(Long userId, MyPageUpdateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.updateProfile(request.getName(), request.getBirthday(), request.getPhone(),
                request.getCaregiverPhone(), request.getIsPushConsent());

        return MyPageResponse.from(user);
    }
}
