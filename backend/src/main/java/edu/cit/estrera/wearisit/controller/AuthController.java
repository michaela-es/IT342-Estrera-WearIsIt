package edu.cit.estrera.wearisit.controller;

import edu.cit.estrera.wearisit.api.ApiResponse;
import edu.cit.estrera.wearisit.dto.*;
import edu.cit.estrera.wearisit.entity.User;
import edu.cit.estrera.wearisit.service.RefreshTokenService;
import edu.cit.estrera.wearisit.service.AuthService;
import edu.cit.estrera.wearisit.util.SecurityUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
}