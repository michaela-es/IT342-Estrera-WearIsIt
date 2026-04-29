package edu.cit.estrera.wearisit.features.clothing_item_management;

import edu.cit.estrera.wearisit.features.user_management.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClothingItemRepository extends JpaRepository<ClothingItem, Long> {

    // Check if item exists and belongs to user
    boolean existsByIdAndUser(Long id, User user);

    // Find item by ID and user
    Optional<ClothingItem> findByIdAndUser(Long id, User user);

    // Get all items for a user
    List<ClothingItem> findByUser(User user);
}