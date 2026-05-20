package edu.cit.estrera.wearisit.features.outfit_management;

import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItem;
import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItemRepository;
import edu.cit.estrera.wearisit.features.clothing_item_management.item_type.ItemType;
import edu.cit.estrera.wearisit.features.image_upload.FileStorageService;
import edu.cit.estrera.wearisit.features.outfit_management.create_outfit.CreateOutfitItemRequest;
import edu.cit.estrera.wearisit.features.outfit_management.create_outfit.CreateOutfitRequest;
import edu.cit.estrera.wearisit.features.outfit_management.get_outfit.OutfitResponse;
import edu.cit.estrera.wearisit.features.outfit_management.mapper.OutfitMapper;
import edu.cit.estrera.wearisit.features.outfit_management.validate_outfit.OutfitValidationResponse;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.features.user_management.UserRepository;
import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OutfitService {

    private final OutfitRepository outfitRepository;
    private final OutfitItemRepository outfitItemRepository;
    private final ClothingItemRepository clothingItemRepository;
    private final UserRepository userRepository;
    private final OutfitMapper outfitMapper;
    private final SecurityUtil securityUtil;
    private final FileStorageService fileStorageService;

    private static final int MIN_ITEMS = 2;

    @Transactional
    public OutfitResponse createOutfit(CreateOutfitRequest request) {
        Long userId = securityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.AUTH_005));

        validateOutfitComposition(request.getItems(), userId);

        Outfit outfit = new Outfit();
        outfit.setOutfitName(request.getOutfitName());
        outfit.setUser(user);
        outfit.setOutfitItems(buildOutfitItems(outfit, request.getItems(), userId));
        outfit.setOutfitWc(sumWc(outfit.getOutfitItems()));

        return outfitMapper.toResponse(outfitRepository.save(outfit));
    }

    private void validateOutfitComposition(List<CreateOutfitItemRequest> items, Long userId) {
        if (items == null || items.size() < MIN_ITEMS) {
            throw new ApiException(ErrorCode.OUTFIT_003,
                    "Outfit must contain at least " + MIN_ITEMS + " items");
        }

        List<Long> itemIds = items.stream()
                .map(CreateOutfitItemRequest::getItemId)
                .toList();

        List<ClothingItem> clothingItems = clothingItemRepository.findAllById(itemIds);

        if (clothingItems.size() != itemIds.size()) {
            throw new ApiException(ErrorCode.OUTFIT_003, "One or more items not found");
        }

        for (ClothingItem item : clothingItems) {
            if (!item.getUser().getUser_id().equals(userId)) {
                throw new ApiException(ErrorCode.OUTFIT_005, "Item doesn't belong to user");
            }
        }

        if (itemIds.size() != new HashSet<>(itemIds).size()) {
            throw new ApiException(ErrorCode.OUTFIT_004, "Duplicate items not allowed");
        }

        Map<ItemType, Long> typeCounts = clothingItems.stream()
                .collect(Collectors.groupingBy(ClothingItem::getType, Collectors.counting()));

        for (Map.Entry<ItemType, Long> entry : typeCounts.entrySet()) {
            ItemType type = entry.getKey();
            int count = entry.getValue().intValue();
            Integer maxAllowed = type.getMaxPerOutfit();

            if (maxAllowed != null && count > maxAllowed) {
                throw new ApiException(ErrorCode.OUTFIT_003,
                        String.format("Too many %s items: maximum %d, got %d",
                                type.getName(), maxAllowed, count));
            }
        }
    }

    private User getCurrentUser() {
        Long userId = securityUtil.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.AUTH_005));
    }

    public OutfitResponse getOutfit(Long outfitId) {
        Outfit outfit = findOwnedOutfit(outfitId);
        return outfitMapper.toResponse(outfit);
    }

    public List<OutfitResponse> getUserOutfits() {
        Long userId = securityUtil.getCurrentUserId();
        return outfitRepository.findByUserId(userId)
                .stream()
                .map(outfitMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OutfitResponse wearOutfit(Long outfitId) {
        Outfit outfit = findOwnedOutfit(outfitId);
        LocalDateTime now = LocalDateTime.now();
        outfit.setLastWorn(now);
        outfit.setOutfitWc(outfit.getOutfitWc()+1);
        outfit.getOutfitItems().forEach(oi -> oi.getClothingItem().setLastWorn(now));
        return outfitMapper.toResponse(outfitRepository.save(outfit));
    }

    public OutfitValidationResponse validateComposition(List<CreateOutfitItemRequest> items) {

        List<String> violations = new ArrayList<>();

        if (items == null || items.size() < 2) {
            violations.add("Outfit must contain at least 2 items");
            return new OutfitValidationResponse(false, violations);
        }

        List<Long> itemIds = items.stream()
                .map(CreateOutfitItemRequest::getItemId)
                .toList();

        if (new HashSet<>(itemIds).size() != itemIds.size()) {
            violations.add(ErrorCode.OUTFIT_004.getMessage());
        }

        List<ClothingItem> clothingItems = clothingItemRepository.findAllById(itemIds);

        if (clothingItems.size() != itemIds.size()) {
            violations.add("One or more items not found");
            return new OutfitValidationResponse(false, violations);
        }

        Map<ItemType, Long> typeCounts = clothingItems.stream()
                .collect(Collectors.groupingBy(
                        ClothingItem::getType,
                        Collectors.counting()
                ));

        for (Map.Entry<ItemType, Long> entry : typeCounts.entrySet()) {

            ItemType type = entry.getKey();
            long count = entry.getValue();

            Integer maxAllowed = type.getMaxPerOutfit();

            if (maxAllowed != null && count > maxAllowed) {
                violations.add(
                        type.getName() + " max is " + maxAllowed + ", found " + count
                );
            }
        }

        return new OutfitValidationResponse(violations.isEmpty(), violations);
    }

    private Outfit findOwnedOutfit(Long outfitId) {
        Long userId = securityUtil.getCurrentUserId();
        return outfitRepository.findByIdAndUserId(outfitId, userId)
                .orElseThrow(() -> new ApiException(ErrorCode.OUTFIT_001));
    }

    private List<OutfitItem> buildOutfitItems(Outfit outfit,
                                              List<CreateOutfitItemRequest> dtos,
                                              Long userId) {
        List<OutfitItem> result = new ArrayList<>();
        for (int i = 0; i < dtos.size(); i++) {
            CreateOutfitItemRequest dto = dtos.get(i);
            ClothingItem ci = clothingItemRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new ApiException(ErrorCode.ITEM_001));
            if (!ci.getUser().getUser_id().equals(userId)) {
                throw new ApiException(ErrorCode.OUTFIT_005);
            }
            OutfitItem oi = new OutfitItem();
            oi.setOutfit(outfit);
            oi.setClothingItem(ci);
            oi.setPosition(dto.getPosition() != null ? dto.getPosition() : i);
            oi.setNotes(dto.getNotes());
            result.add(oi);
        }
        return result;
    }

    private int sumWc(List<OutfitItem> items) {
        return items.stream()
                .mapToInt(oi -> oi.getClothingItem().getItemWc())
                .sum();
    }

    @Transactional
    public void deleteOutfit(Long outfitId) {
        User currentUser = securityUtil.getCurrentUser();
        Long userId = currentUser.getUser_id();

        Outfit outfit = outfitRepository.findByIdAndUser_Id(outfitId, userId)
                .orElseThrow(() -> new ApiException(ErrorCode.OUTFIT_001));

        if (outfit.getCoverImageUrl() != null && !outfit.getCoverImageUrl().isEmpty()) {
            try {
                fileStorageService.deleteFile(outfit.getCoverImageUrl());
            } catch (Exception e) {
                System.err.println("Failed to delete cloud file during outfit deletion: " + e.getMessage());
            }
        }

        outfitRepository.delete(outfit);
    }

    @Transactional
    public OutfitResponse editOutfit(Long outfitId, EditOutfitRequest request) {
        Long userId = securityUtil.getCurrentUserId();
        Outfit outfit = findOwnedOutfit(outfitId);

        if (request.getOutfitName() != null) {
            outfit.setOutfitName(request.getOutfitName());
        }

        if (request.getItems() != null) {
            outfitItemRepository.deleteAll(outfit.getOutfitItems());
            outfit.getOutfitItems().clear();

            List<OutfitItem> newItems = buildOutfitItems(outfit, request.getItems(), userId);
            outfit.getOutfitItems().addAll(newItems);
            outfit.setOutfitWc(sumWc(newItems));
        }

        return outfitMapper.toResponse(outfitRepository.save(outfit));
    }
}
