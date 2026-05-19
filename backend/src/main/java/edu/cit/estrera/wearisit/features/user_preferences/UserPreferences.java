package edu.cit.estrera.wearisit.features.user_preferences;

import edu.cit.estrera.wearisit.features.user_management.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_prefs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "accent_color", columnDefinition = "VARCHAR(20) DEFAULT '#1D4ED8'")
    private String accentColor = "#1D4ED8";

    @Column(name = "theme_mode", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'LIGHT'")
    @Enumerated(EnumType.STRING)
    private ThemeMode themeMode = ThemeMode.LIGHT;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }


}