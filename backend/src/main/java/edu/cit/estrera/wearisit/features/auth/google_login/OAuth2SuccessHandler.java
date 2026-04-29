package edu.cit.estrera.wearisit.features.auth.google_login;

import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.features.user_management.UserRepository;
import edu.cit.estrera.wearisit.infrastructure.security.jwt.JwtService;
import edu.cit.estrera.wearisit.infrastructure.security.jwt.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Value("${FRONTEND_URL:http://localhost:5173}")
    private String frontendUrl;

    public OAuth2SuccessHandler(UserRepository userRepository,
                                JwtService jwtService,
                                RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String googleId = oAuth2User.getAttribute("sub");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(name != null ? name : email);
                    newUser.setPassword(null);
                    newUser.setProvider("GOOGLE");
                    newUser.setProviderId(googleId);
                    newUser.setEnabled(true);
                    newUser.setIs_active(true);
                    newUser.setRole("USER");
                    newUser.setCreated_at(LocalDateTime.now());
                    return userRepository.save(newUser);
                });

        user.setLast_login(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user.getUser_id());
        String refreshToken = refreshTokenService.createRefreshToken(user.getUser_id());

        String redirectUrl = String.format(
                "%s/oauth-callback#accessToken=%s&refreshToken=%s",
                frontendUrl,
                accessToken,
                refreshToken
        );

        response.sendRedirect(redirectUrl);
    }
}