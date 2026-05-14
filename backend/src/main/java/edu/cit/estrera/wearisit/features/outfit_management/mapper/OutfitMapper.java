package edu.cit.estrera.wearisit.features.outfit_management.mapper;

import edu.cit.estrera.wearisit.features.outfit_management.Outfit;
import edu.cit.estrera.wearisit.features.outfit_management.OutfitItem;
import edu.cit.estrera.wearisit.features.outfit_management.get_outfit.OutfitItemResponse;
import edu.cit.estrera.wearisit.features.outfit_management.get_outfit.OutfitResponse;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class OutfitMapper {

    public OutfitResponse toResponse(Outfit outfit) {
        if (outfit == null) return null;

        OutfitResponse dto = new OutfitResponse();
        dto.setId(outfit.getId());
        dto.setOutfitName(outfit.getOutfitName());
        dto.setOutfitWc(outfit.getOutfitWc());
        dto.setUserId(outfit.getUser().getUser_id());
        dto.setUsername(outfit.getUser().getUsername());
        dto.setLastWorn(outfit.getLastWorn());
        dto.setCoverImageUrl(outfit.getCoverImageUrl());
        dto.setCreatedAt(outfit.getCreatedAt());
        dto.setUpdatedAt(outfit.getUpdatedAt());

        if (outfit.getOutfitItems() != null) {
            dto.setItems(outfit.getOutfitItems().stream()
                    .map(this::toItemResponse)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private OutfitItemResponse toItemResponse(OutfitItem oi) {
        OutfitItemResponse dto = new OutfitItemResponse();
        dto.setId(oi.getId());
        dto.setItemId(oi.getClothingItem().getId());
        dto.setItemName(oi.getClothingItem().getItemName());
        dto.setImageUrl(oi.getClothingItem().getImageUrl());
        dto.setItemTypeName(oi.getClothingItem().getType().getName());
        dto.setItemTypeId(oi.getClothingItem().getType().getId());
        dto.setPosition(oi.getPosition());
        dto.setNotes(oi.getNotes());
        return dto;
    }
}
