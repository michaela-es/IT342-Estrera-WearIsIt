package edu.cit.estrera.wearisit.features.outfit_management;

import edu.cit.estrera.wearisit.features.outfit_management.create_outfit.CreateOutfitItemRequest;
import edu.cit.estrera.wearisit.features.outfit_management.create_outfit.CreateOutfitRequest;
import edu.cit.estrera.wearisit.features.outfit_management.get_outfit.OutfitResponse;
import edu.cit.estrera.wearisit.features.outfit_management.validate_outfit.OutfitValidationResponse;
import edu.cit.estrera.wearisit.infrastructure.api.response.ApiResponse;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/outfits")
@RequiredArgsConstructor
public class OutfitController {

    private final OutfitService outfitService;
    private final SecurityUtil securityUtil;

    @PostMapping("/new")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<OutfitResponse>> createOutfit(
            @Valid @RequestBody CreateOutfitRequest request) {
        OutfitResponse response = outfitService.createOutfit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityUtil.isOutfitOwner(#id)")
    public ResponseEntity<ApiResponse<OutfitResponse>> getOutfit(@PathVariable Long id) {
        OutfitResponse response = outfitService.getOutfit(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<OutfitResponse>>> getUserOutfits() {
        List<OutfitResponse> responses = outfitService.getUserOutfits();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/{id}/wear")
    @PreAuthorize("@securityUtil.isOutfitOwner(#id)")
    public ResponseEntity<ApiResponse<OutfitResponse>> wearOutfit(@PathVariable Long id) {
        OutfitResponse response = outfitService.wearOutfit(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/validate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<OutfitValidationResponse>> validateOutfit(
            @Valid @RequestBody List<CreateOutfitItemRequest> items) {
        OutfitValidationResponse validation = outfitService.validateComposition(items);
        return ResponseEntity.ok(ApiResponse.success(validation));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityUtil.isItemOwner(#id)")
    public ResponseEntity<ApiResponse<String>> deleteOutfit(@PathVariable Long id) {

        outfitService.deleteOutfit(id);

        return ResponseEntity.ok(
                ApiResponse.success("Outfit deleted successfully")
        );
    }
}
