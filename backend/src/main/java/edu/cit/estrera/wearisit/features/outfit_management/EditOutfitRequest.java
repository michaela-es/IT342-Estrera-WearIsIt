package edu.cit.estrera.wearisit.features.outfit_management;

import edu.cit.estrera.wearisit.features.outfit_management.create_outfit.CreateOutfitItemRequest;
import lombok.Data;
import java.util.List;

@Data
public class EditOutfitRequest {
    private String outfitName;
    private List<CreateOutfitItemRequest> items;  }

