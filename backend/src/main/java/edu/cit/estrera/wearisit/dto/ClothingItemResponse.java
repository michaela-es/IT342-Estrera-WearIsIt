package edu.cit.estrera.wearisit.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ClothingItemResponse {
    private Long id;
    private String itemName;
    private String imageUrl;
    private Integer itemWc;
    private Long typeId;
    private String typeName;
    private Map<String, List<String>> properties;
    private LocalDateTime lastWorn;
    private LocalDateTime createdAt;
}