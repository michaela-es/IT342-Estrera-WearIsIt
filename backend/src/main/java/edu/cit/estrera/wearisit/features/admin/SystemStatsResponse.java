package edu.cit.estrera.wearisit.features.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemStatsResponse {

    private OverviewStats overview;
    private UserStats userStats;
    private SystemStats systemStats;

    @Data
    @Builder
    public static class OverviewStats {
        private long totalUsers;
        private long totalItems;
        private long totalOutfits;
        private long totalWears;
        private long totalCategories;
        private long totalTags;
    }

    @Data
    @Builder
    public static class UserStats {
        private long activeToday;
        private long activeThisWeek;
        private long activeThisMonth;
        private long newUsersThisMonth;
    }

    @Data
    @Builder
    public static class SystemStats {
        private double averageItemsPerUser;
        private MostActiveUser mostActiveUser;
    }

    @Data
    @Builder
    public static class MostActiveUser {
        private Long id;
        private String username;
        private Integer totalWears;
    }
}