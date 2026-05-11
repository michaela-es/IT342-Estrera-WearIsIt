package edu.cit.estrera.wearisit.features.clothing_item_management;

import lombok.Data;
import java.util.List;

@Data
public class EditClothingItemRequest {
    private String itemName;
    private Long typeId;
    private List<Long> categoryIds;
    private List<Long> tagIds;
}