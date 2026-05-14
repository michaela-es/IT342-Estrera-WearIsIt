package edu.cit.estrera.wearisit.features.outfit_management.validate_outfit;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class OutfitValidationResponse {
    private boolean valid;
    private List<String> violations;
}
