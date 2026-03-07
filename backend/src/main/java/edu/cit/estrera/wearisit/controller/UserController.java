package edu.cit.estrera.wearisit.controller;

import edu.cit.estrera.wearisit.dto.ProfileResponse;
import edu.cit.estrera.wearisit.entity.User;
import edu.cit.estrera.wearisit.repository.UserRepository;
import edu.cit.estrera.wearisit.util.SecurityUtil;
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