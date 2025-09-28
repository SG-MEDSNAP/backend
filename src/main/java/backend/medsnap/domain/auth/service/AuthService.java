package backend.medsnap.domain.auth.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import backend.medsnap.domain.auth.dto.request.LoginRequest;
import backend.medsnap.domain.auth.dto.request.LogoutRequest;
import backend.medsnap.domain.auth.dto.request.RefreshRequest;
import backend.medsnap.domain.auth.dto.request.SignupRequest;
import backend.medsnap.domain.auth.dto.token.TokenPair;
import backend.medsnap.domain.auth.exception.InvalidRefreshTokenException;
import backend.medsnap.domain.auth.exception.SocialAccountAlreadyExistsException;
import backend.medsnap.domain.auth.exception.SocialAccountNotFoundException;
import backend.medsnap.domain.auth.jwt.JwtTokenProvider;
import backend.medsnap.domain.user.entity.Provider;
import backend.medsnap.domain.user.entity.SocialAccount;
import backend.medsnap.domain.user.entity.User;
import backend.medsnap.domain.user.repository.SocialAccountRepository;
import backend.medsnap.domain.user.repository.UserRepository;
import backend.medsnap.global.crypto.AesGcmEncryptor;
import backend.medsnap.global.dto.ApiResponse;
import backend.medsnap.infra.oauth.exception.OidcVerificationException;
import backend.medsnap.infra.oauth.verifier.AbstractOidcVerifier;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final Map<String, AbstractOidcVerifier> oidcVerifiers;
    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AesGcmEncryptor aesGcmEncryptor;

    public ApiResponse<TokenPair> login(LoginRequest request) {

        // ID 토큰 검증
        DecodedJWT decodedJWT = verifyIdToken(request.getProvider(), request.getIdToken());
        String providerUserId = decodedJWT.getSubject();

        // 소셜 계정 조회
        SocialAccount socialAccount =
                socialAccountRepository
                        .findByProviderAndProviderUserId(request.getProvider(), providerUserId)
                        .orElseThrow(SocialAccountNotFoundException::new);

        User user = socialAccount.getUser();

        // 자체 JWT 발급
        TokenPair tokenPair = jwtTokenProvider.createTokenPair(user);

        // Refresh Token만 암호화하여 DB에 저장
        String encryptedRefreshToken = aesGcmEncryptor.encrypt(tokenPair.getRefreshToken());
        user.updateRefreshToken(encryptedRefreshToken);

        return ApiResponse.success(tokenPair);
    }

    public ApiResponse<TokenPair> signup(SignupRequest request) {

        // ID 토큰 검증
        DecodedJWT decodedJWT = verifyIdToken(request.getProvider(), request.getIdToken());
        String providerUserId = decodedJWT.getSubject();

        if (socialAccountRepository
                .findByProviderAndProviderUserId(request.getProvider(), providerUserId)
                .isPresent()) {
            throw new SocialAccountAlreadyExistsException();
        }

        // 새로운 User와 SocialAccount 생성 및 저장
        User newUser =
                User.builder()
                        .name(request.getName())
                        .birthday(request.getBirthday())
                        .phone(request.getPhone())
                        .caregiverPhone(null) // request.getCaregiverPhone() 대신 null
                        .isPushConsent(request.getIsPushConsent())
                        .build();
        userRepository.save(newUser);

        SocialAccount newSocialAccount =
                SocialAccount.builder()
                        .provider(request.getProvider())
                        .providerUserId(providerUserId)
                        .user(newUser)
                        .build();
        socialAccountRepository.save(newSocialAccount);

        // 자체 JWT 발급
        TokenPair tokenPair = jwtTokenProvider.createTokenPair(newUser);

        // Refresh Token만 암호화하여 DB에 저장
        String encryptedRefreshToken = aesGcmEncryptor.encrypt(tokenPair.getRefreshToken());
        newUser.updateRefreshToken(encryptedRefreshToken);

        return ApiResponse.success(tokenPair);
    }

    public ApiResponse<Void> logout(LogoutRequest request) {
        String provided = request.getRefreshToken();

        // refresh 토큰 검증
        DecodedJWT jwt;
        try {
            jwt = jwtTokenProvider.verifyRefreshToken(provided);
        } catch (JWTVerificationException e) {
            throw new InvalidRefreshTokenException();
        }
        Long userId = Long.valueOf(jwt.getSubject());

        // 사용자 조회
        User user = userRepository.findById(userId).orElseThrow(InvalidRefreshTokenException::new);

        // 저장된 refresh 토큰 확인
        String storedEnc = user.getRefreshToken();
        if (storedEnc == null) {
            // 이미 로그아웃된 상태도 성공으로 처리
            return ApiResponse.success(null);
        }

        String storedPlain = aesGcmEncryptor.decrypt(storedEnc);

        // 상수시간 비교로 동일성 확인
        if (!constantTimeEquals(provided, storedPlain)) {
            throw new InvalidRefreshTokenException();
        }

        // 무효화: DB의 refreshToken 제거
        user.updateRefreshToken(null);

        return ApiResponse.success(null);
    }

    public ApiResponse<TokenPair> refresh(RefreshRequest request) {
        String provided = request.getRefreshToken();

        // refresh 토큰 검증
        DecodedJWT jwt;
        try {
            jwt = jwtTokenProvider.verifyRefreshToken(provided);
        } catch (JWTVerificationException e) {
            throw new InvalidRefreshTokenException();
        }
        Long userId = Long.valueOf(jwt.getSubject());

        // 사용자 조회
        User user = userRepository.findById(userId).orElseThrow(InvalidRefreshTokenException::new);

        // 저장된 refresh 와 상수시간 비교
        String storedEnc = user.getRefreshToken();
        if (storedEnc == null) throw new InvalidRefreshTokenException();
        String storedPlain = aesGcmEncryptor.decrypt(storedEnc);

        if (!constantTimeEquals(provided, storedPlain)) {
            throw new InvalidRefreshTokenException();
        }

        // 새 토큰 페어 발급
        TokenPair newPair = jwtTokenProvider.createTokenPair(user);
        String newEnc = aesGcmEncryptor.encrypt(newPair.getRefreshToken());
        user.updateRefreshToken(newEnc);

        return ApiResponse.success(newPair);
    }

    private DecodedJWT verifyIdToken(Provider provider, String idToken) {
        String beanName = provider.name().toLowerCase() + "OidcVerifier";
        AbstractOidcVerifier verifier = oidcVerifiers.get(beanName);

        if (verifier == null) {
            throw new OidcVerificationException("지원하지 않는 OIDC Provider입니다: " + provider, null);
        }
        return verifier.verify(idToken);
    }

    /** 상수시간 비교 */
    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) return false;
        int r = 0;
        for (int i = 0; i < a.length(); i++) r |= a.charAt(i) ^ b.charAt(i);
        return r == 0;
    }
}
