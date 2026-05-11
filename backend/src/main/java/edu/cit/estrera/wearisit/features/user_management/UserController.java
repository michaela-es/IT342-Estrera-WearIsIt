package edu.cit.estrera.wearisit.features.user_management;

import edu.cit.estrera.wearisit.features.auth.ProfileResponse;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepository;
    private final  SecurityUtil securityUtil;

    public UserController(UserRepository userRepository, SecurityUtil securityUtil) {
        this.userRepository = userRepository;
        this.securityUtil = securityUtil;
    }

    @GetMapping("/user/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            User user = securityUtil.getCurrentUser();
            ProfileResponse response = new ProfileResponse(
                    user.getUsername(),
                    user.getEnabled(),
                    user.getEmail()
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403)
                    .body(Collections.singletonMap("message", "Need authentication to access this endpoint"));
        }
    }
}