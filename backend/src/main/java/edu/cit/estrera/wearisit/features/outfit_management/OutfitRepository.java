package edu.cit.estrera.wearisit.features.outfit_management;

import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface OutfitRepository extends JpaRepository<Outfit, Long> {

    List<Outfit> findByUserId(Long userId);

    Optional<Outfit> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT o FROM Outfit o WHERE o.user.id = :userId ORDER BY o.lastWorn DESC")
    List<Outfit> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);
    Optional<Outfit> findByIdAndUser_Id(Long itemId, Long userId);

}
