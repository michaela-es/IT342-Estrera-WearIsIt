package edu.cit.estrera.wearisit.dto;

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