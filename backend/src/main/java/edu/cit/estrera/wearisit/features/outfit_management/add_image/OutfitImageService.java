package edu.cit.estrera.wearisit.features.outfit_management.add_image;

import edu.cit.estrera.wearisit.features.image_upload.FileStorageService;
import edu.cit.estrera.wearisit.features.outfit_management.Outfit;
import edu.cit.estrera.wearisit.features.outfit_management.OutfitRepository;
import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutfitImageService {

    private final OutfitRepository outfitRepository;
    private final FileStorageService fileStorageService;
    private final SecurityUtil securityUtil;

    @Transactional
    public String saveOutfitImage(Long outfitId, MultipartFile file) throws IOException {
        Outfit outfit = getOwnedOutfit(outfitId);
        String imageUrl = uploadImage(file);
        outfit.setCoverImageUrl(imageUrl);
        outfitRepository.save(outfit);
        return imageUrl;
    }

    @Transactional
    public String updateOutfitImage(Long outfitId, MultipartFile file) throws IOException {
        Outfit outfit = getOwnedOutfit(outfitId);

        if (outfit.getCoverImageUrl() != null && !outfit.getCoverImageUrl().isEmpty()) {
            fileStorageService.deleteFile(outfit.getCoverImageUrl());
        }

        String imageUrl = uploadImage(file);
        outfit.setCoverImageUrl(imageUrl);
        outfitRepository.save(outfit);
        return imageUrl;
    }

    @Transactional
    public void deleteOutfitImage(Long outfitId) {
        Outfit outfit = getOwnedOutfit(outfitId);

        if (outfit.getCoverImageUrl() != null && !outfit.getCoverImageUrl().isEmpty()) {
            fileStorageService.deleteFile(outfit.getCoverImageUrl());
            outfit.setCoverImageUrl(null);
            outfitRepository.save(outfit);
        }
    }

    private Outfit getOwnedOutfit(Long outfitId) {
        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(() -> new ApiException(ErrorCode.OUTFIT_001));

        if (!outfit.getUser().getUser_id().equals(securityUtil.getCurrentUser().getUser_id())) {
            throw new ApiException(ErrorCode.OUTFIT_002);
        }
        return outfit;
    }

    private String uploadImage(MultipartFile file) throws IOException {
        String userFolderSecret = UUID.randomUUID().toString();
        String folderPath = "users/" + userFolderSecret + "/outfits";
        return fileStorageService.uploadFile(file, folderPath);
    }
}