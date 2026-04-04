package edu.cit.estrera.wearisit.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import edu.cit.estrera.wearisit.dto.AuthResponse;
import edu.cit.estrera.wearisit.entity.User;
import edu.cit.estrera.wearisit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final GoogleIdTokenVerifier verifier;

    public GoogleAuthService(UserRepository userRepository,
                             JwtService jwtService,
                             RefreshTokenService refreshTokenService,
                             @Value("${spring.security.oauth2.client.registration.google.client-id}") String clientId) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;

        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    @Transactional
    public AuthResponse processGoogleLogin(String idToken) {
        GoogleIdToken.Payload payload = verifyGoogleToken(idToken);

        String email = payload.getEmail();
        String googleId = payload.getSubject();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");
        boolean emailVerified = payload.getEmailVerified();

        if (!emailVerified) {
            throw new RuntimeException("Email not verified by Google");
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(email, googleId, name, picture));

        user.setLast_login(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user.getUser_id());
        String refreshToken = refreshTokenService.createRefreshToken(user.getUser_id());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private GoogleIdToken.Payload verifyGoogleToken(String idToken) {
        try {
            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                throw new RuntimeException("Invalid Google token");
            }
            return token.getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify Google token: " + e.getMessage());
        }
    }

    private User createNewUser(String email, String googleId, String name, String picture) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(name != null ? name : email.split("@")[0]);
        newUser.setPassword(null);
        newUser.setProvider("GOOGLE");
        newUser.setProviderId(googleId);
        newUser.setEnabled(true);
        newUser.setIs_active(true);
        newUser.setRole("USER");
        newUser.setCreated_at(LocalDateTime.now());

        return userRepository.save(newUser);
    }
}