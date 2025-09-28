package backend.medsnap.domain.pushToken.controller;

import backend.medsnap.domain.auth.dto.token.CustomUserDetails;
import backend.medsnap.domain.pushToken.dto.request.UpsertPushTokenRequest;
import backend.medsnap.domain.pushToken.dto.response.PushTokenResponse;
import backend.medsnap.domain.pushToken.entity.PushToken;
import backend.medsnap.domain.pushToken.service.PushTokenService;
import backend.medsnap.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/push-tokens")
public class PushTokenController implements PushTokenSwagger {

    private final PushTokenService pushTokenService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<PushTokenResponse>> upsertPushToken(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpsertPushTokenRequest request
    ) {
        PushToken pushToken = pushTokenService.upsertPushToken(userDetails.getId(), request);
        PushTokenResponse response = new PushTokenResponse(pushToken.getToken(), pushToken.getPlatform());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, response));
    }
}
