package edu.cit.estrera.wearisit.features.category_management;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private int itemCount;
    private LocalDateTime createdAt;
}