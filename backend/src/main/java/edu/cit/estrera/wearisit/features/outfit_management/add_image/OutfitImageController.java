package edu.cit.estrera.wearisit.features.outfit_management.add_image;

import edu.cit.estrera.wearisit.infrastructure.api.response.ApiResponse;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/outfits")
@RequiredArgsConstructor
public class OutfitImageController {

    private final OutfitImageService outfitImageService;
    private final SecurityUtil securityUtil;

    @PostMapping("/{id}/image")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadOutfitImage(
            @PathVariable("id") Long outfitId,
            @RequestParam("image") MultipartFile file) throws IOException {

        String currentUserEmail = securityUtil.getCurrentUser().getEmail();
        String imageUrl = outfitImageService.saveOutfitImage(outfitId, file);

        Map<String, Object> data = Map.of(
                "outfitId", outfitId,
                "imageUrl", imageUrl
        );

        return ResponseEntity.ok(ApiResponse.success(data));
    }
}