package backend.medsnap.domain.user.controller;

import backend.medsnap.domain.auth.dto.token.CustomUserDetails;
import backend.medsnap.domain.user.dto.request.MyPageUpdateRequest;
import backend.medsnap.domain.user.dto.response.MyPageResponse;
import backend.medsnap.domain.user.service.UserService;
import backend.medsnap.global.dto.ApiResponse;
import backend.medsnap.global.entity.BaseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.attribute.UserPrincipal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserSwagger {

    private final UserService userService;

    @Override
    @PutMapping("/mypage")
    public ResponseEntity<ApiResponse<MyPageResponse>> updateMyPage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MyPageUpdateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(userService.updateMyPage(userDetails.getId(), request))
        );

    }
}
