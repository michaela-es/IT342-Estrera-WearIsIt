package edu.cit.estrera.wearisit.test.auth_tests;

import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.features.auth.AuthResponse;
import edu.cit.estrera.wearisit.features.auth.RegisterRequest;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.features.user_management.UserRepository;
import edu.cit.estrera.wearisit.infrastructure.security.jwt.JwtService;
import edu.cit.estrera.wearisit.features.auth.AuthService;
import edu.cit.estrera.wearisit.infrastructure.security.jwt.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegisterUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest validRequest;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        validRequest = new RegisterRequest();
        validRequest.setEmail("test@example.com");
        validRequest.setUsername("testuser");
        validRequest.setPassword("password123");
    }

    // Test 1: Valid fields - should succeed
    @Test
    void register_ValidFields_ShouldSucceed() {
        // Arrange
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(validRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(validRequest.getPassword())).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(validRequest.getEmail());
        savedUser.setUsername(validRequest.getUsername());
        savedUser.setEnabled(false);
        savedUser.setIs_active(true);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateAccessToken(1L)).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(1L)).thenReturn("refresh-token");

        // Act
        AuthResponse response = authService.register(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertNotNull(response.getUser());
        assertEquals("test@example.com", response.getUser().getEmail());
        assertEquals("testuser", response.getUser().getUsername());

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    // Test 2: Empty email - should throw AUTH_006
    @Test
    void register_EmptyEmail_ShouldThrowAuth006() {
        // Arrange
        RegisterRequest emptyEmailRequest = new RegisterRequest();
        emptyEmailRequest.setEmail("");
        emptyEmailRequest.setUsername("testuser");
        emptyEmailRequest.setPassword("password123");

        // Act & Assert
        ApiException ex = assertThrows(ApiException.class,
                () -> authService.register(emptyEmailRequest));

        assertEquals(ErrorCode.AUTH_006, ex.getErrorCode());
        assertEquals("Email format is invalid", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // Test 3: Null email - should throw AUTH_006
    @Test
    void register_NullEmail_ShouldThrowAuth006() {
        // Arrange
        RegisterRequest nullEmailRequest = new RegisterRequest();
        nullEmailRequest.setEmail(null);
        nullEmailRequest.setUsername("testuser");
        nullEmailRequest.setPassword("password123");

        // Act & Assert
        ApiException ex = assertThrows(ApiException.class,
                () -> authService.register(nullEmailRequest));

        assertEquals(ErrorCode.AUTH_006, ex.getErrorCode());
        assertEquals("Email format is invalid", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // Test 4: Invalid email format - should throw AUTH_006
    @Test
    void register_InvalidEmailFormat_ShouldThrowAuth006() {
        // Arrange
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setUsername("testuser");
        invalidRequest.setPassword("password123");

        // Act & Assert
        ApiException ex = assertThrows(ApiException.class,
                () -> authService.register(invalidRequest));

        assertEquals(ErrorCode.AUTH_006, ex.getErrorCode());
        assertEquals("Email format is invalid", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // Test 5: Empty password - should throw AUTH_007
    @Test
    void register_EmptyPassword_ShouldThrowAuth007() {
        // Arrange
        RegisterRequest emptyPasswordRequest = new RegisterRequest();
        emptyPasswordRequest.setEmail("test@example.com");
        emptyPasswordRequest.setUsername("testuser");
        emptyPasswordRequest.setPassword("");

        // Act & Assert
        ApiException ex = assertThrows(ApiException.class,
                () -> authService.register(emptyPasswordRequest));

        assertEquals(ErrorCode.AUTH_007, ex.getErrorCode());
        assertEquals("Password cannot be empty", ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    // Test 6: Null password - should throw AUTH_007
    @Test
    void register_NullPassword_ShouldThrowAuth007() {
        // Arrange
        RegisterRequest nullPasswordRequest = new RegisterRequest();
        nullPasswordRequest.setEmail("test@example.com");
        nullPasswordRequest.setUsername("testuser");
        nullPasswordRequest.setPassword(null);

        // Act & Assert
        ApiException ex = assertThrows(ApiException.class,
                () -> authService.register(nullPasswordRequest));

        assertEquals(ErrorCode.AUTH_007, ex.getErrorCode());
        assertEquals("Password cannot be empty", ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    // Test 7: Empty username - should throw AUTH_008
    @Test
    void register_EmptyUsername_ShouldThrowAuth008() {
        // Arrange
        RegisterRequest emptyUsernameRequest = new RegisterRequest();
        emptyUsernameRequest.setEmail("test@example.com");
        emptyUsernameRequest.setUsername("");
        emptyUsernameRequest.setPassword("password123");

        // Act & Assert
        ApiException ex = assertThrows(ApiException.class,
                () -> authService.register(emptyUsernameRequest));

        assertEquals(ErrorCode.AUTH_008, ex.getErrorCode());
        assertEquals("Username cannot be empty", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // Test 8: Null username - should throw AUTH_008
    @Test
    void register_NullUsername_ShouldThrowAuth008() {
        // Arrange
        RegisterRequest nullUsernameRequest = new RegisterRequest();
        nullUsernameRequest.setEmail("test@example.com");
        nullUsernameRequest.setUsername(null);
        nullUsernameRequest.setPassword("password123");

        // Act & Assert
        ApiException ex = assertThrows(ApiException.class,
                () -> authService.register(nullUsernameRequest));

        assertEquals(ErrorCode.AUTH_008, ex.getErrorCode());
        assertEquals("Username cannot be empty", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // Test 9: Email already exists - should throw AUTH_004
    @Test
    void register_EmailAlreadyExists_ShouldThrowAuth004() {
        // Arrange
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        // Act & Assert
        ApiException ex = assertThrows(ApiException.class,
                () -> authService.register(validRequest));

        assertEquals(ErrorCode.AUTH_004, ex.getErrorCode());
        assertEquals("Email already registered", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // Test 10: Username already exists - should throw AUTH_009
    @Test
    void register_UsernameAlreadyExists_ShouldThrowAuth009() {
        // Arrange
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(validRequest.getUsername())).thenReturn(true);

        // Act & Assert
        ApiException ex = assertThrows(ApiException.class,
                () -> authService.register(validRequest));

        assertEquals(ErrorCode.AUTH_009, ex.getErrorCode());
        assertEquals("Username already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }
}