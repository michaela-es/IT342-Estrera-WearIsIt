package edu.cit.estrera.wearisit.test;

import edu.cit.estrera.wearisit.features.auth.*;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.features.user_management.UserRepository;
import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.infrastructure.security.jwt.JwtService;
import edu.cit.estrera.wearisit.infrastructure.security.jwt.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LogInUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_ValidUsername_ShouldReturnTokens() {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("testuser");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateAccessToken(1L)).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(1L)).thenReturn("refresh-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("testuser", response.getUser().getUsername());

        verify(userRepository).save(user);
    }

    @Test
    void login_ValidEmail_ShouldReturnTokens() {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("test@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateAccessToken(1L)).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(1L)).thenReturn("refresh-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("test@example.com", response.getUser().getEmail());
    }

    @Test
    void login_UserNotFound_ShouldThrowAuth001() {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("unknown");
        request.setPassword("password123");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("unknown")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> authService.login(request));

        assertEquals(ErrorCode.AUTH_001, ex.getErrorCode());
    }

    @Test
    void login_WrongPassword_ShouldThrowAuth001() {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("testuser");
        request.setPassword("wrongpass");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "encodedPassword")).thenReturn(false);

        ApiException ex = assertThrows(ApiException.class,
                () -> authService.login(request));

        assertEquals(ErrorCode.AUTH_001, ex.getErrorCode());
    }

    @Test
    void login_EmailFallbackLogic_ShouldUseEmailSearch() {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("test@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtService.generateAccessToken(1L)).thenReturn("token");
        when(refreshTokenService.createRefreshToken(1L)).thenReturn("refresh");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("test@example.com", response.getUser().getEmail());
    }
}