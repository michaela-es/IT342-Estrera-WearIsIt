package edu.cit.estrera.wearisit.features.user_preferences;

import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.infrastructure.api.response.ApiResponse;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/preferences")
@RequiredArgsConstructor
public class UserPreferencesController {

    private final UserPreferencesService preferencesService;
    private final SecurityUtil securityUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<UserPreferencesResponseDTO>> getPreferences() {
        User user = securityUtil.getCurrentUser();
        UserPreferencesResponseDTO preferences = preferencesService.getPreferencesByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.success(preferences));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<UserPreferencesResponseDTO>> updatePreferences(
            @Valid @RequestBody UserPreferencesUpdateDTO updateDTO) {
        User user = securityUtil.getCurrentUser();
        UserPreferencesResponseDTO updated = preferencesService.updatePreferences(user.getId(), updateDTO);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }
}