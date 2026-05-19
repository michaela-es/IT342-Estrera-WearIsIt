package edu.cit.estrera.wearisit.features.user_preferences;

import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.features.user_management.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserPreferencesService {

    private final UserPreferencesRepository preferencesRepository;
    private final UserRepository userRepository;
    @Transactional
    public UserPreferencesResponseDTO updatePreferences(Long userId, UserPreferencesUpdateDTO updateDTO) {
        UserPreferences preferences = getPreferencesEntityByUserId(userId);

        Optional.ofNullable(updateDTO.getAccentColor()).ifPresent(preferences::setAccentColor);
        Optional.ofNullable(updateDTO.getThemeMode()).ifPresent(preferences::setThemeMode);

        return convertToResponseDTO(preferencesRepository.save(preferences));
    }

    public UserPreferencesResponseDTO getPreferencesByUserId(Long userId) {
        UserPreferences preferences = getPreferencesEntityByUserId(userId);
        return convertToResponseDTO(preferences);
    }

    private UserPreferences getPreferencesEntityByUserId(Long userId) {
        return preferencesRepository.findByUser_Id(userId)
                .orElseGet(() -> createDefaultPreferences(userId));
    }

    private UserPreferencesResponseDTO convertToResponseDTO(UserPreferences preferences) {
        return UserPreferencesResponseDTO.builder()
                .accentColor(preferences.getAccentColor())
                .themeMode(preferences.getThemeMode())
                .build();
    }

    public UserPreferences createDefaultPreferences(Long userId) {
        UserPreferences preferences = new UserPreferences();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        preferences.setUser(user);
        preferences.setThemeMode(ThemeMode.LIGHT);
        preferences.setAccentColor("#1D4ED8");

        return preferencesRepository.save(preferences);
    }
}