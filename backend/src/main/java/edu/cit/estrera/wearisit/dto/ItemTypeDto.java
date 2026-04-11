package edu.cit.estrera.wearisit.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemTypeDto {
    private Long id;
    private String name;
}