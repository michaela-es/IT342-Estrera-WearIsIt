package edu.cit.estrera.wearisit.features.admin;

import edu.cit.estrera.wearisit.features.auth.AuthResponse;
import edu.cit.estrera.wearisit.features.auth.AuthService;
import edu.cit.estrera.wearisit.features.auth.RegisterRequest;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.features.user_management.UserRepository;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.security.jwt.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository repository;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public boolean isEmailWhitelisted(String email) {
        if (email == null) return false;
        return repository.existsByEmailIgnoreCase(email.trim());
    }

    @Transactional
    public AuthResponse registerAdminIfWhitelisted(RegisterRequest request) {
        String email = request.getEmail();

        if (!isEmailWhitelisted(email)) {
            throw new ApiException(ErrorCode.ADMIN_001);
        }

        AuthResponse resp = authService.register(request);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.DB_001));
        user.setRole("ADMIN");
        userRepository.save(user);

        String adminAccess = jwtService.generateAccessToken(user.getUser_id());

        return AuthResponse.builder()
                .accessToken(adminAccess)
                .refreshToken(resp.getRefreshToken())
                .user(resp.getUser())
                .build();
    }
}
