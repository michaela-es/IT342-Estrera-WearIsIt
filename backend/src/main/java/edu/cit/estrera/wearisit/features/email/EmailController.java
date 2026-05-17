package edu.cit.estrera.wearisit.features.email;

import edu.cit.estrera.wearisit.features.auth.AuthService;
import edu.cit.estrera.wearisit.infrastructure.api.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email/")
@CrossOrigin(origins = "*")
public class EmailController {
    private final AuthService authService;

    public EmailController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("Email Verified Successfully"));
    }
}
