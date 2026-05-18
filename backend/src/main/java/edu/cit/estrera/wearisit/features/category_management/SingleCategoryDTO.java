package edu.cit.estrera.wearisit.features.category_management;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SingleCategoryDTO {
    private Long id;
    private String name;
    private Integer itemCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CategoryTagDto> tags;

    @Data
    @Builder
    public static class CategoryTagDto {
        private Long id;
        private String name;
    }
}