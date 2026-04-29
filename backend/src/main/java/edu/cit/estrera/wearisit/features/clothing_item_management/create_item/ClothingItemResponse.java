package edu.cit.estrera.wearisit.features.clothing_item_management.create_item;

import edu.cit.estrera.wearisit.features.tag_management.TagDto;
import edu.cit.estrera.wearisit.features.category_management.CategoryDto;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ClothingItemResponse {
    private Long id;
    private String itemName;
    private String imageUrl;
    private Integer itemWc;
    private Long typeId;
    private String typeName;
    private List<CategoryDto> categories;
    private List<TagDto> tags;
    private LocalDateTime lastWorn;
    private LocalDateTime createdAt;
}

