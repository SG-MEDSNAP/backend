package backend.medsnap.domain.auth.service;

import backend.medsnap.domain.auth.dto.request.LoginRequest;
import backend.medsnap.domain.auth.dto.request.SignupRequest;
import backend.medsnap.domain.auth.dto.token.TokenPair;
import backend.medsnap.domain.auth.exception.SocialAccountAlreadyExistsException;
import backend.medsnap.domain.auth.exception.SocialAccountNotFoundException;
import backend.medsnap.domain.auth.jwt.JwtTokenProvider;
import backend.medsnap.domain.user.entity.Provider;
import backend.medsnap.domain.user.entity.SocialAccount;
import backend.medsnap.domain.user.entity.User;
import backend.medsnap.domain.user.repository.SocialAccountRepository;
import backend.medsnap.domain.user.repository.UserRepository;
import backend.medsnap.global.dto.ApiResponse;
import backend.medsnap.infra.oauth.exception.OidcVerificationException;
import backend.medsnap.infra.oauth.verifier.AbstractOidcVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final Map<String, AbstractOidcVerifier> oidcVerifiers;
    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public ApiResponse<TokenPair> login(LoginRequest request) {

        // ID 토큰 검증
        DecodedJWT decodedJWT = verifyIdToken(request.getProvider(), request.getIdToken());
        String providerUserId = decodedJWT.getSubject();

        // 소셜 계정 조회
        SocialAccount socialAccount = socialAccountRepository
                .findByProviderAndProviderUserId(request.getProvider(), providerUserId)
                .orElseThrow(SocialAccountNotFoundException::new);

        User user = socialAccount.getUser();

        // 자체 JWT 발급
        TokenPair tokenPair = jwtTokenProvider.createTokenPair(user);
        user.updateTokens(tokenPair.getAccessToken(), tokenPair.getRefreshToken());

        return ApiResponse.success(tokenPair);
    }

    public ApiResponse<TokenPair> signup(SignupRequest request) {

        // ID 토큰 검증
        DecodedJWT decodedJWT = verifyIdToken(request.getProvider(), request.getIdToken());
        String providerUserId = decodedJWT.getSubject();

        if (socialAccountRepository.findByProviderAndProviderUserId(request.getProvider(), providerUserId).isPresent()) {
            throw new SocialAccountAlreadyExistsException();
        }

        // 새로운 User와 SocialAccount 생성 및 저장
        User newUser = User.builder()
                .birthday(request.getBirthday())
                .phone(request.getPhone())
                .caregiverPhone(request.getCaregiverPhone())
                .isPushConsent(request.getIsPushConsent())
                .build();
        userRepository.save(newUser);

        SocialAccount newSocialAccount = SocialAccount.builder()
                .provider(request.getProvider())
                .providerUserId(providerUserId)
                .user(newUser)
                .build();
        socialAccountRepository.save(newSocialAccount);

        // 자체 JWT 발급
        TokenPair tokenPair = jwtTokenProvider.createTokenPair(newUser);
        newUser.updateTokens(tokenPair.getAccessToken(), tokenPair.getRefreshToken());

        return ApiResponse.success(tokenPair);
    }

    private DecodedJWT verifyIdToken(Provider provider, String idToken) {
        AbstractOidcVerifier verifier = oidcVerifiers.get(provider.name().toLowerCase() + "OidcVerifier");

        if (verifier == null) {
            throw new OidcVerificationException("지원하지 않는 OIDC Provider입니다: " + provider, null);
        }
        return verifier.verify(idToken);
    }
}
