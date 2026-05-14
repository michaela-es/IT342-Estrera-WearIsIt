package edu.cit.estrera.wearisit.features.outfit_management;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OutfitItemRepository extends JpaRepository<OutfitItem, Long> {
    List<OutfitItem> findByOutfitId(Long outfitId);
    void deleteByOutfitId(Long outfitId);
    List<OutfitItem> findByClothingItemId(Long clothingItemId);
}
