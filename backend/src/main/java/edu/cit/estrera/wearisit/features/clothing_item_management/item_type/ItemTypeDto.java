package edu.cit.estrera.wearisit.features.clothing_item_management.item_type;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemTypeDto {
    private Long id;
    private String name;
}