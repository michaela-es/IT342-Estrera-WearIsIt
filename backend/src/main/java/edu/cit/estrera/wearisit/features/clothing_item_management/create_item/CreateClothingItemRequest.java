package edu.cit.estrera.wearisit.features.clothing_item_management.create_item;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreateClothingItemRequest {
    @NotBlank(message = "Item name is required")
    private String itemName;

    @NotNull(message = "Type is required")
    private Long typeId;

    private List<Long> categoryIds;

    private List<Long> tagIds;
}