package edu.cit.estrera.wearisit.features.tag_management;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagDto {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
}
