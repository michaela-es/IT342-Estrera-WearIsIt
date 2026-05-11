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
    public ResponseEntity<ApiResponse<Category>> editCategory(@PathVariable Long categoryId,
                                                              @RequestBody EditCategoryRequest request) {
        User user = securityUtil.getCurrentUser();
        Category category = categoryService.editCategory(categoryId, request, user);
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long categoryId) {
        User user = securityUtil.getCurrentUser();
        categoryService.deleteCategory(categoryId, user);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}