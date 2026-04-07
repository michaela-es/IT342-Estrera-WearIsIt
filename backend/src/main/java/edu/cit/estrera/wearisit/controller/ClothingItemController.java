package edu.cit.estrera.wearisit.controller;

import edu.cit.estrera.wearisit.api.ApiResponse;
import edu.cit.estrera.wearisit.dto.ClothingItemResponse;
import edu.cit.estrera.wearisit.dto.CreateClothingItemRequest;
import edu.cit.estrera.wearisit.service.ClothingItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")

public class ClothingItemController {

    private final ClothingItemService clothingItemService;
    public ClothingItemController(ClothingItemService clothingItemService) {
        this.clothingItemService = clothingItemService;
    }
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ClothingItemResponse>> createClothingItem(
            @Valid @RequestBody CreateClothingItemRequest request) {

        ClothingItemResponse response = clothingItemService.createClothingItem(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ClothingItemResponse>>> getUserClothingItems() {
        List<ClothingItemResponse> responses = clothingItemService.getUserClothingItems();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
    @GetMapping("/{id}")
    @PreAuthorize("@securityUtil.isItemOwner(#id)")
    public ResponseEntity<ApiResponse<ClothingItemResponse>> getClothingItem(@PathVariable Long id) {
        ClothingItemResponse response = clothingItemService.getClothingItem(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}