package edu.cit.estrera.wearisit.features.outfit_management;

import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItem;
import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItemRepository;
import edu.cit.estrera.wearisit.features.clothing_item_management.item_type.ItemType;
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

    private static final int MAX_TOP = 1;
    private static final int MAX_BOTTOM = 1;
    private static final int MAX_SHOES = 1;
    private static final int MAX_ACCESSORIES = 5;

    @Transactional
    public OutfitResponse createOutfit(CreateOutfitRequest request) {
        Long userId = securityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.AUTH_005));

        OutfitValidationResponse validation = validateComposition(request.getItems());
        if (!validation.isValid()) {
            throw new ApiException(ErrorCode.OUTFIT_003,
                    String.join("; ", validation.getViolations()));
        }

        Outfit outfit = new Outfit();
        outfit.setOutfitName(request.getOutfitName());
        outfit.setCoverImageUrl(request.getCoverImageUrl());
        outfit.setUser(user);

        List<OutfitItem> outfitItems = buildOutfitItems(outfit, request.getItems(), userId);
        outfit.setOutfitItems(outfitItems);
        outfit.setOutfitWc(sumWc(outfitItems));

        return outfitMapper.toResponse(outfitRepository.save(outfit));
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
        outfit.getOutfitItems().forEach(oi -> oi.getClothingItem().setLastWorn(now));
        return outfitMapper.toResponse(outfitRepository.save(outfit));
    }

    public OutfitValidationResponse validateComposition(List<CreateOutfitItemRequest> items) {
        List<String> violations = new ArrayList<>();

        if (items == null || items.isEmpty()) {
            violations.add("Outfit must contain at least 2 items");
            return new OutfitValidationResponse(false, violations);
        }

        List<Long> itemIds = items.stream()
                .map(CreateOutfitItemRequest::getItemId)
                .collect(Collectors.toList());

        // Duplicate check
        if (new HashSet<>(itemIds).size() != itemIds.size()) {
            violations.add(ErrorCode.OUTFIT_004.getMessage());
        }

        List<ClothingItem> clothingItems = clothingItemRepository.findAllById(itemIds);
        if (clothingItems.size() != itemIds.size()) {
            violations.add("One or more items not found");
            return new OutfitValidationResponse(false, violations);
        }

        // Count by type
        Map<Long, Integer> typeCount = new HashMap<>();
        Map<Long, String> typeNames = new HashMap<>();
        for (ClothingItem item : clothingItems) {
            ItemType type = item.getType();
            typeCount.merge(type.getId(), 1, Integer::sum);
            typeNames.put(type.getId(), type.getName());
        }

        for (Map.Entry<Long, Integer> entry : typeCount.entrySet()) {
            String typeName = typeNames.get(entry.getKey()).toUpperCase();
            int count = entry.getValue();
            switch (typeName) {
                case "TOP"       -> { if (count > MAX_TOP)        violations.add("Max " + MAX_TOP + " TOP item(s), found " + count); }
                case "BOTTOM"    -> { if (count > MAX_BOTTOM)     violations.add("Max " + MAX_BOTTOM + " BOTTOM item(s), found " + count); }
                case "SHOES"     -> { if (count > MAX_SHOES)      violations.add("Max " + MAX_SHOES + " SHOES item(s), found " + count); }
                case "ACCESSORY" -> { if (count > MAX_ACCESSORIES)violations.add("Max " + MAX_ACCESSORIES + " ACCESSORY item(s), found " + count); }
                default          -> violations.add(ErrorCode.OUTFIT_006.getMessage() + ": " + typeName);
            }
        }

        return new OutfitValidationResponse(violations.isEmpty(), violations);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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
}
