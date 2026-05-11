package edu.cit.estrera.wearisit.features.tag_management;

import lombok.Data;

@Data
public class EditTagRequest {
    private String name;
    private Long categoryId;
}
