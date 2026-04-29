package edu.cit.estrera.wearisit.features.auth;

import edu.cit.estrera.wearisit.features.user_management.UserResponse;
import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.features.user_management.UserRepository;
import edu.cit.estrera.wearisit.infrastructure.security.jwt.JwtService;
import edu.cit.estrera.wearisit.infrastructure.security.jwt.RefreshTokenService;
import edu.cit.estrera.wearisit.shared.util.regex.EmailValidator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (request.getEmail() == null || !EmailValidator.isValid(request.getEmail()))
            throw new ApiException(ErrorCode.AUTH_006);

        if (request.getPassword() == null || request.getPassword().isEmpty())
            throw new ApiException(ErrorCode.AUTH_007);

        if (userRepository.existsByEmail(request.getEmail())){
            throw new ApiException(ErrorCode.AUTH_004);
        }

        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new ApiException(ErrorCode.AUTH_008);
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException(ErrorCode.AUTH_009);
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(false);
        user.setIs_active(true);

        user = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user.getUser_id());
        String refreshToken = refreshTokenService.createRefreshToken(user.getUser_id());

        UserResponse userResponse = mapToUserResponse(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userResponse)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .orElseGet(() -> {
                    if (request.getUsernameOrEmail().contains("@")) {
                        return userRepository.findByEmail(request.getUsernameOrEmail()).orElse(null);
                    }
                    return null;
                });

        if (user == null) {
            throw new ApiException(ErrorCode.AUTH_001);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorCode.AUTH_001);
        }

        user.setLast_login(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user.getUser_id());
        String refreshToken = refreshTokenService.createRefreshToken(user.getUser_id());

        UserResponse userResponse = mapToUserResponse(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userResponse)
                .build();
    }

    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
    }

    public UserResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.AUTH_005));
        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse userResponse = UserResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
        return userResponse;
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.DB_001));
    }

}