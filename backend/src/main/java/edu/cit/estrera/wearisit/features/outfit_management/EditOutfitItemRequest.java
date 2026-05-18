package edu.cit.estrera.wearisit.features.outfit_management;

import lombok.Data;

@Data
public class EditOutfitItemRequest {
    private Long itemId;
    private Integer position;
    private String notes;
}
