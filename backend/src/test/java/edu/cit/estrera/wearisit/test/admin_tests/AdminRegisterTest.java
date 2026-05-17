package edu.cit.estrera.wearisit.test.admin_tests;

import edu.cit.estrera.wearisit.features.admin.AdminRepository;
import edu.cit.estrera.wearisit.features.admin.AdminService;
import edu.cit.estrera.wearisit.features.auth.AuthResponse;
import edu.cit.estrera.wearisit.features.auth.AuthService;
import edu.cit.estrera.wearisit.features.auth.RegisterRequest;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.features.user_management.UserRepository;
import edu.cit.estrera.wearisit.features.user_management.UserResponse;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AdminRegisterTest {

    private AdminRepository adminRepository;
    private AuthService authService;
    private UserRepository userRepository;
    private JwtService jwtService;
    private AdminService adminService;

    @BeforeEach
    void setup() {
        adminRepository = mock(AdminRepository.class);
        authService = mock(AuthService.class);
        userRepository = mock(UserRepository.class);
        jwtService = mock(JwtService.class);
        adminService = new AdminService(adminRepository, authService, userRepository, jwtService);
    }

    @Test
    void registerAdmin_success_promotesUserAndReturnsAdminToken_andRoleIsAdmin() {
        // arrange
        String email = "allowed@example.com";
        RegisterRequest req = new RegisterRequest();
        req.setEmail(email);
        req.setUsername("u");
        req.setPassword("p");

        when(adminRepository.existsByEmailIgnoreCase(email)).thenReturn(true);

        UserResponse userResponse = UserResponse.builder()
                .email(email)
                .username("u")
                .build();

        AuthResponse baseResp = AuthResponse.builder()
                .accessToken("user-token")
                .refreshToken("refresh-token")
                .user(userResponse)
                .build();
        when(authService.register(req)).thenReturn(baseResp);

        User savedUser = new User();
        savedUser.setId(123L);
        savedUser.setEmail(email);
        savedUser.setRole("USER");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(savedUser));
        when(jwtService.generateAccessToken(123L)).thenReturn("admin-token");

        // act
        AuthResponse result = adminService.registerAdminIfWhitelisted(req);

        // assert tokens
        assertThat(result.getAccessToken()).isEqualTo("admin-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");

        // verify userRepository.save was called and the saved user's role was set to ADMIN
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getRole()).isEqualTo("ADMIN");

        // also ensure the response user is the same as returned by authService.register (username/email)
        assertThat(result.getUser().getEmail()).isEqualTo(userResponse.getEmail());
        assertThat(result.getUser().getUsername()).isEqualTo(userResponse.getUsername());

        verify(authService).register(req);
        verify(jwtService).generateAccessToken(123L);
    }

    @Test
    void registerAdmin_notWhitelisted_throwsAdminError_andNoSideEffects() {
        // arrange
        String email = "notallowed@example.com";
        RegisterRequest req = new RegisterRequest();
        req.setEmail(email);

        when(adminRepository.existsByEmailIgnoreCase(email)).thenReturn(false);

        // act / assert
        assertThatThrownBy(() -> adminService.registerAdminIfWhitelisted(req))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_001);

        verifyNoInteractions(authService, userRepository, jwtService);
    }
}
