package edu.cit.estrera.wearisit.controller;

import edu.cit.estrera.wearisit.dto.*;
import edu.cit.estrera.wearisit.entity.User;
import edu.cit.estrera.wearisit.service.RefreshTokenService;
import edu.cit.estrera.wearisit.service.UserService;
import edu.cit.estrera.wearisit.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final SecurityUtil securityUtil;

    public AuthController(UserService userService, RefreshTokenService refreshTokenService, SecurityUtil securityUtil) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            Map<String, Object> response = userService.register(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = userService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        // TODO: Implement refresh token logic
        return ResponseEntity.status(501).build();
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            User user = securityUtil.getCurrentUser();
            refreshTokenService.revokeAllUserTokens(user.getUser_id());
            SecurityContextHolder.clearContext();

            return ResponseEntity.ok(Collections.singletonMap("message", "Logout successful"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403)
                    .body(Collections.singletonMap("message", "Need authentication to logout"));
        }
    }




}