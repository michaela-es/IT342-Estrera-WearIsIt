package edu.cit.estrera.wearisit.features.tag_management;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagItemDto {
    private Long id;
    private String itemName;
}