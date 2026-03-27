package edu.cit.estrera.wearisit.service;

import edu.cit.estrera.wearisit.api.ApiException;
import edu.cit.estrera.wearisit.api.ErrorCode;
import edu.cit.estrera.wearisit.entity.Category;
import edu.cit.estrera.wearisit.entity.User;
import edu.cit.estrera.wearisit.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category getOrCreateCategory(String name, User user, Long userId) {
        return categoryRepository.findByNameAndUser_Id (name, userId)
                .orElseGet(() -> createCategory(name, user));
    }

    public Category createCategory(String name, User user) {
        Category category = new Category();
        category.setName(name);
        category.setUser(user);
        return categoryRepository.save(category);
    }

    public String getCategoryName(Long categoryId) {
        return categoryId != null ?
                categoryRepository.findById(categoryId).map(Category::getName).orElse(null) : null;
    }


    public List<Category> getCategoriesByIdsAndUser(List<Long> categoryIds, Long userId) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Category> categories = categoryRepository.findByIdInAndUser_Id(categoryIds, userId);

        if (categories.size() != categoryIds.size()) {
            throw new ApiException(ErrorCode.ITEM_003, "One or more categories not found or don't belong to you");
        }

        return categories;
    }
}