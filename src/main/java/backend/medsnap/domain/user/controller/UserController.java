package backend.medsnap.domain.user.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import backend.medsnap.domain.auth.dto.token.CustomUserDetails;
import backend.medsnap.domain.user.dto.request.MyPageUpdateRequest;
import backend.medsnap.domain.user.dto.response.MyPageResponse;
import backend.medsnap.domain.user.dto.response.UserInfoResponse;
import backend.medsnap.domain.user.service.UserService;
import backend.medsnap.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserSwagger {

    private final UserService userService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserInfo(userDetails.getId())));
    }

    @Override
    @PutMapping("/mypage")
    public ResponseEntity<ApiResponse<MyPageResponse>> updateMyPage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MyPageUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(userService.updateMyPage(userDetails.getId(), request)));
    }
}
