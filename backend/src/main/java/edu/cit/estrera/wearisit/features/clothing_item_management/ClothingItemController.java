package edu.cit.estrera.wearisit.features.clothing_item_management;

import edu.cit.estrera.wearisit.features.clothing_item_management.create_item.ClothingItemResponse;
import edu.cit.estrera.wearisit.features.clothing_item_management.create_item.CreateClothingItemRequest;
import edu.cit.estrera.wearisit.infrastructure.api.response.ApiResponse;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ClothingItemController {

    private final ClothingItemService clothingItemService;
    private final SecurityUtil securityUtil;

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

    @PostMapping("/{id}/wear")
    @PreAuthorize("@securityUtil.isItemOwner(#id)")
    public ResponseEntity<ApiResponse<ClothingItemResponse>> wearClothingItem(@PathVariable Long id) {
        ClothingItemResponse response = clothingItemService.wearClothingItem(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}