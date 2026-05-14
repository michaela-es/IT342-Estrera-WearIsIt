package edu.cit.estrera.wearisit.features.outfit_management.get_outfit;

import lombok.Data;

@Data
public class OutfitItemResponse {
    private Long id;
    private Long itemId;
    private String itemName;
    private String imageUrl;
    private String itemTypeName;
    private Long itemTypeId;
    private Integer position;
    private String notes;
}
