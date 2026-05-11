package edu.cit.estrera.wearisit.features.tag_management;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagResponse {

    private Long id;
    private String name;
    private Long categoryId;

    public static TagResponse from(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .categoryId(tag.getCategory().getId())
                .build();
    }
}