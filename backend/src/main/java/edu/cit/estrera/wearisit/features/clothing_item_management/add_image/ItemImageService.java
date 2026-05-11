package edu.cit.estrera.wearisit.features.clothing_item_management.add_image;

import edu.cit.estrera.wearisit.features.image_upload.FileStorageService;
import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItem;
import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItemRepository;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemImageService {

    private final ClothingItemRepository clothingItemRepository;
    private final FileStorageService fileStorageService;
    private final SecurityUtil securityUtil;

    @Transactional
    public String saveItemImage(Long itemId, MultipartFile file) throws IOException {

        ClothingItem item = clothingItemRepository.findById(itemId)
                .orElseThrow(() -> new ApiException(ErrorCode.ITEM_001));

        if (!item.getUser().getUser_id().equals(securityUtil.getCurrentUser().getUser_id())) {
            throw new ApiException(ErrorCode.ITEM_002);
        }

        String userFolderSecret = UUID.randomUUID().toString();
        String folderPath = "users/" + userFolderSecret + "/items";

        String imageUrl = fileStorageService.uploadFile(file, folderPath);

        item.setImageUrl(imageUrl);
        clothingItemRepository.save(item);

        return imageUrl;
    }
}