package edu.cit.estrera.wearisit.features.tag_management;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTagRequest {

    @NotBlank
    private String name;

    @NotNull
    private Long categoryId;
}