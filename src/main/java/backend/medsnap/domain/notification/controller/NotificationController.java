package backend.medsnap.domain.notification.controller;

import backend.medsnap.domain.auth.dto.token.CustomUserDetails;
import backend.medsnap.domain.notification.dto.request.NotificationCreateRequest;
import backend.medsnap.domain.notification.dto.response.NotificationCreateResponse;
import backend.medsnap.domain.notification.service.NotificationService;
import backend.medsnap.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController implements NotificationSwagger {

    private final NotificationService notificationService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<NotificationCreateResponse>> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid NotificationCreateRequest request
    ) {
        Long notificationId = notificationService.createNotification(userDetails.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(new NotificationCreateResponse(notificationId)));
    }
}
