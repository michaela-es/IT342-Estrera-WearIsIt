package edu.cit.estrera.wearisit.controller;

import edu.cit.estrera.wearisit.api.ApiResponse;
import edu.cit.estrera.wearisit.service.ItemImageService;
import edu.cit.estrera.wearisit.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemImageController {

    private final ItemImageService itemImageService;
    private final SecurityUtil securityUtil;

    @PostMapping("/{id}/image")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadItemImage(
            @PathVariable("id") Long itemId,
            @RequestParam("image") MultipartFile file) throws IOException {

        String currentUserEmail = securityUtil.getCurrentUser().getEmail();
        String imageUrl = itemImageService.saveItemImage(itemId, file);

        Map<String, Object> data = Map.of(
                "itemId", itemId,
                "imageUrl", imageUrl
        );

        return ResponseEntity.ok(ApiResponse.success(data));
    }
}