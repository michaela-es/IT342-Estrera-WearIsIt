package edu.cit.estrera.wearisit.features.outfit_management.create_outfit;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOutfitItemRequest {

    @NotNull(message = "Item ID is required")
    private Long itemId;

    private Integer position;

    private String notes;
}
