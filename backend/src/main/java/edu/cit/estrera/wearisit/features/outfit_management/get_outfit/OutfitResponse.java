package edu.cit.estrera.wearisit.features.outfit_management.get_outfit;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OutfitResponse {
    private Long id;
    private String outfitName;
    private Integer outfitWc;
    private Long userId;
    private String username;
    private LocalDateTime lastWorn;
    private String coverImageUrl;
    private List<OutfitItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

