package edu.cit.estrera.wearisit.features.user_preferences;

import edu.cit.estrera.wearisit.features.user_management.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    Optional<UserPreferences> findByUser_Id(Long userId);
    boolean existsByUser_Id(Long userId);
}