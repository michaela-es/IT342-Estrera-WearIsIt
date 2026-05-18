package edu.cit.estrera.wearisit.features.user_management;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserStats {
    private int totalItems;
    private int totalOutfits;
    private int totalWears;
    private String lastActive;
}