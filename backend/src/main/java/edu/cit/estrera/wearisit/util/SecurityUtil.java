package edu.cit.estrera.wearisit.util;

import edu.cit.estrera.wearisit.entity.Category;
import edu.cit.estrera.wearisit.entity.User;
import edu.cit.estrera.wearisit.repository.CategoryRepository;
import edu.cit.estrera.wearisit.repository.ClothingItemRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtil {
    private final ClothingItemRepository clothingItemRepository;
    private final CategoryRepository categoryRepository;
    public SecurityUtil(ClothingItemRepository clothingItemRepository, CategoryRepository categoryRepository) {
        this.clothingItemRepository = clothingItemRepository;
        this.categoryRepository = categoryRepository;
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
}