package edu.cit.estrera.wearisit.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
public class CreateClothingItemRequest {
    @NotBlank(message = "Item name is required")
    private String itemName;

    @NotNull(message = "Type ID is required")
    private Long typeId;

    private List<Long> categoryIds;

    private List<Long> tagIds;
    private Map<String, String> properties;
}