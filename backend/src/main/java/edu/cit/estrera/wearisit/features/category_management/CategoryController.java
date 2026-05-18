package edu.cit.estrera.wearisit.features.category_management;

import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.infrastructure.api.response.ApiResponse;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final SecurityUtil securityUtil;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {

        Category category = categoryService.createCategory(
                request.getName(),
                securityUtil.getCurrentUser()
        );

        Map<String, Object> categoryData = new HashMap<>();
        categoryData.put("id", category.getId());
        categoryData.put("name", category.getName());
        categoryData.put("itemCount", 0);
        categoryData.put("createdAt", category.getCreatedAt());

        return ResponseEntity.ok(ApiResponse.success(categoryData));
    }

    @GetMapping("/with-tags")
    public ResponseEntity<ApiResponse<List<CategoryWithTagsDTO>>> getCategoriesWithTags() {
        List<CategoryWithTagsDTO> categories = categoryService.getCategoriesWithTagsForCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryDto>> editCategory(
            @PathVariable Long categoryId,
            @RequestBody EditCategoryRequest request) {

        User user = securityUtil.getCurrentUser();
        Category category = categoryService.editCategory(categoryId, request, user);

        CategoryDto dto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();

        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long categoryId) {
        User user = securityUtil.getCurrentUser();
        categoryService.deleteCategory(categoryId, user);

        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
        User user = securityUtil.getCurrentUser();
        List<Category> categories = categoryService.getCategoriesByUser(user.getUser_id());

        List<CategoryDto> categoryDtos = categories.stream()
                .map(category -> CategoryDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(categoryDtos));
    }

    @GetMapping("/{categoryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SingleCategoryDTO>> getCategoryById(@PathVariable Long categoryId) {
        User user = securityUtil.getCurrentUser();
        Category category = categoryService.getCategoryByIdAndUser(categoryId, user.getUser_id());

        SingleCategoryDTO dto = SingleCategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .itemCount(category.getItems() != null ? category.getItems().size() : 0)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .tags(category.getTags().stream()
                        .map(tag -> SingleCategoryDTO.CategoryTagDto.builder()
                                .id(tag.getId())
                                .name(tag.getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return ResponseEntity.ok(ApiResponse.success(dto));
    }

}