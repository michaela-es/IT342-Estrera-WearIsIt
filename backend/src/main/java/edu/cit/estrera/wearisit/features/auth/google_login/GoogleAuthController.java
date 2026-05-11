package edu.cit.estrera.wearisit.features.auth.google_login;

import edu.cit.estrera.wearisit.features.auth.AuthResponse;
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