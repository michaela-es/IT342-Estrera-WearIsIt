package edu.cit.estrera.wearisit.infrastructure.security;

import edu.cit.estrera.wearisit.features.outfit_management.OutfitRepository;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.features.category_management.CategoryRepository;
import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItemRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {
    private final ClothingItemRepository clothingItemRepository;
    private final CategoryRepository categoryRepository;
    private final OutfitRepository outfitRepository;

    public SecurityUtil(ClothingItemRepository clothingItemRepository, CategoryRepository categoryRepository, OutfitRepository outfitRepository) {
        this.clothingItemRepository = clothingItemRepository;
        this.categoryRepository = categoryRepository;
        this.outfitRepository = outfitRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new RuntimeException("Not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return (User) principal;
        }

        throw new RuntimeException("Invalid principal type: " +
                (principal != null ? principal.getClass().getName() : "null"));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getUser_id();
    }

    public String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }
    public boolean isItemOwner(Long itemId) {
        return clothingItemRepository.existsByIdAndUser(itemId, getCurrentUser());
    }
    public boolean isCategoryOwner(Long categoryId) {
        try {
            return categoryRepository.existsByIdAndUser_Id(categoryId, getCurrentUserId());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOutfitOwner(Long outfitId) {
        Long currentUserId = getCurrentUserId();
        return outfitRepository.findById(outfitId)
                .map(outfit -> outfit.getUser().getUser_id().equals(currentUserId))
                .orElse(false);
    }
}