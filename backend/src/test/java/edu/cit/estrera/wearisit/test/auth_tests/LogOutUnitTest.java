package edu.cit.estrera.wearisit.test.auth_tests;

import edu.cit.estrera.wearisit.features.auth.AuthService;
import edu.cit.estrera.wearisit.infrastructure.security.jwt.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class LogOutUnitTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void logout_ShouldRevokeRefreshToken() {
        // Arrange
        String refreshToken = "sample-refresh-token";

        // Act
        authService.logout(refreshToken);

        // Assert
        verify(refreshTokenService, times(1))
                .revokeRefreshToken(refreshToken);
    }
}