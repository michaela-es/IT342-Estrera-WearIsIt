package edu.cit.estrera.wearisit.features.category_management;

import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.features.tag_management.TagDto;
import edu.cit.estrera.wearisit.features.tag_management.Tag;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.features.tag_management.TagRepository;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final SecurityUtil securityUtil;
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

    @Transactional(readOnly = true)
    public List<CategoryWithTagsDTO> getCategoriesWithTagsForCurrentUser() {
        Long userId = securityUtil.getCurrentUser().getUser_id();

        List<Category> categories = categoryRepository.findByUser_Id(userId);

        return categories.stream()
                .map(category -> {
                    List<Tag> tags = tagRepository.findByCategoryIdAndUser_Id(category.getId(), userId);

                    List<TagDto> tagDtos = tags.stream()
                            .map(tag -> TagDto.builder()
                                    .id(tag.getId())
                                    .name(tag.getName())
                                    .categoryId(category.getId())
                                    .categoryName(category.getName())
                                    .build())
                            .collect(Collectors.toList());

                    return CategoryWithTagsDTO.builder()
                            .id(category.getId())
                            .name(category.getName())
                            .tags(tagDtos)
                            .build();
                })
                .collect(Collectors.toList());
    }
}