package edu.cit.estrera.wearisit.features.category_management;

import edu.cit.estrera.wearisit.features.tag_management.TagDto;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CategoryWithTagsDTO {
    private Long id;
    private String name;
    private List<TagDto> tags;
}