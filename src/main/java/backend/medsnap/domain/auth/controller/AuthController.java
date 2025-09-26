package backend.medsnap.domain.auth.controller;

import backend.medsnap.domain.auth.dto.request.LogoutRequest;
import backend.medsnap.domain.auth.dto.request.RefreshRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.medsnap.domain.auth.dto.request.LoginRequest;
import backend.medsnap.domain.auth.dto.request.SignupRequest;
import backend.medsnap.domain.auth.dto.token.TokenPair;
import backend.medsnap.domain.auth.service.AuthService;
import backend.medsnap.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthSwagger {

    private final AuthService authService;

    @Override
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenPair>> login(@Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @Override
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenPair>> signup(
            @Valid @RequestBody SignupRequest request) {

        return ResponseEntity.ok(authService.signup(request));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {

        return ResponseEntity.ok(authService.logout(request));
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenPair>> refresh(@Valid @RequestBody RefreshRequest request) {

        return ResponseEntity.ok(authService.refresh(request));
    }
}
