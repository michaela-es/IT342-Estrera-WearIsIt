package edu.cit.estrera.wearisit.features.user_preferences;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesResponseDTO {
    private String accentColor;
    private ThemeMode themeMode;
}
