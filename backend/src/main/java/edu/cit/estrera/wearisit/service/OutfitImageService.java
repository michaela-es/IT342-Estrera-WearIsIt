//package edu.cit.estrera.wearisit.service;
//import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
//import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.IOException;
//
//@Service
//public class OutfitImageService {
//
//    @Autowired private OutfitRepository outfitRepository;
//    @Autowired private FileStorageService fileStorageService;
//
//    public String saveOutfitImage(Long outfitId, MultipartFile file, String email) throws IOException {
//        Outfit outfit = outfitRepository.findById(outfitId)
//                .orElseThrow(() -> new ApiException(ErrorCode.OUTFIT_NOT_FOUND));
//
//        if (!outfit.getUser().getEmail().equals(email)) throw new ApiException(ErrorCode.ITEM_002);
//
//        String folderPath = "users/" + outfit.getUser().getId() + "/outfits";
//
//        String imageUrl = fileStorageService.uploadFile(file, folderPath);
//
//        outfit.setImageUrl(imageUrl);
//        outfitRepository.save(outfit);
//        return imageUrl;
//    }
//}

// TODO: implement