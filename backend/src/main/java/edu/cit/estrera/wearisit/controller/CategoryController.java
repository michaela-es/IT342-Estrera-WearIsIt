package edu.cit.estrera.wearisit.controller;

import edu.cit.estrera.wearisit.api.ApiResponse;
import edu.cit.estrera.wearisit.dto.CategoryWithTagsDTO;
import edu.cit.estrera.wearisit.dto.CreateCategoryRequest;
import edu.cit.estrera.wearisit.entity.Category;
import edu.cit.estrera.wearisit.service.CategoryService;
import edu.cit.estrera.wearisit.util.SecurityUtil;
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
    public ResponseEntity<List<CategoryWithTagsDTO>> getCategoriesWithTags() {
        List<CategoryWithTagsDTO> categories = categoryService.getCategoriesWithTagsForCurrentUser();
        return ResponseEntity.ok(categories);
    }
}
