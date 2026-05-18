package edu.cit.estrera.wearisit.features.tag_management;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TagDetailDto {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private Integer itemCount;
    private List<TagItemDto> items;
}
