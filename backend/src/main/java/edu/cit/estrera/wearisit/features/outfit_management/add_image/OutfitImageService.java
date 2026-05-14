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

        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(() -> new ApiException(ErrorCode.OUTFIT_001));

        if (!outfit.getUser().getUser_id().equals(securityUtil.getCurrentUser().getUser_id())) {
            throw new ApiException(ErrorCode.OUTFIT_002);
        }

        String userFolderSecret = UUID.randomUUID().toString();
        String folderPath = "users/" + userFolderSecret + "/outfits";

        String imageUrl = fileStorageService.uploadFile(file, folderPath);

        outfit.setCoverImageUrl(imageUrl);
        outfitRepository.save(outfit);

        return imageUrl;
    }
}