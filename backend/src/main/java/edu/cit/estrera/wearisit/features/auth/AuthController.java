package edu.cit.estrera.wearisit.features.auth;

import edu.cit.estrera.wearisit.infrastructure.api.response.ApiResponse;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.infrastructure.security.jwt.RefreshTokenService;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final SecurityUtil securityUtil;

    public AuthController(AuthService authService,
                          RefreshTokenService refreshTokenService,
                          SecurityUtil securityUtil) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

//    @PostMapping("/refresh")
//    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody RefreshTokenRequest request) {
//
//        AuthResponse response = refreshTokenService.refresh(request.getRefreshToken());
//
//        return ResponseEntity.ok(ApiResponse.success(response));
//    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {

        User user = securityUtil.getCurrentUser();

        refreshTokenService.revokeAllUserTokens(user.getUser_id());
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(
                ApiResponse.success("Logout successful")
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Map<String, String>>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "If an account exists, a password reset email has been sent")));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Map<String, String>>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "Password successfully reset")));
    }
}