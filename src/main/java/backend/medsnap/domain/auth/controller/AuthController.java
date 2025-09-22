package backend.medsnap.domain.auth.controller;

import backend.medsnap.domain.auth.dto.request.LoginRequest;
import backend.medsnap.domain.auth.dto.request.SignupRequest;
import backend.medsnap.domain.auth.dto.token.TokenPair;
import backend.medsnap.domain.auth.service.AuthService;
import backend.medsnap.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthSwagger{

    private final AuthService authService;

    @Override
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenPair>> login(@Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @Override
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenPair>> signup(@Valid @RequestBody SignupRequest request) {

        return ResponseEntity.ok(authService.signup(request));
    }
}
