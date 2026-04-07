package edu.cit.estrera.wearisit.controller;

import edu.cit.estrera.wearisit.dto.AuthResponse;
import edu.cit.estrera.wearisit.service.GoogleAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    public GoogleAuthController(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody Map<String, String> payload) {
        String idToken = payload.get("idToken");
        AuthResponse response = googleAuthService.processGoogleLogin(idToken);
        return ResponseEntity.ok(response);
    }
}