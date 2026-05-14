package edu.cit.estrera.wearisit.features.outfit_management.create_outfit;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class CreateOutfitRequest {

    @NotBlank(message = "Outfit name is required")
    private String outfitName;

    private String coverImageUrl;

    @NotNull(message = "Items list is required")
    @Size(min = 2, max = 8, message = "Outfit must have between 2 and 8 items")
    @Valid
    private List<CreateOutfitItemRequest> items;
}
