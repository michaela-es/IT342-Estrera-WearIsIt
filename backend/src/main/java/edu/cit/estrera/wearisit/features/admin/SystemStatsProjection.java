package edu.cit.estrera.wearisit.features.admin;

public interface SystemStatsProjection {

    Long getTotalUsers();
    Long getTotalItems();
    Long getTotalOutfits();
    Long getTotalWears();
    Long getTotalCategories();
    Long getTotalTags();

    Long getActiveToday();
    Long getActiveWeek();
    Long getActiveMonth();
    Long getNewUsersMonth();

    Long getMostActiveUserId();
    String getMostActiveUsername();
    Integer getMostActiveUserWears();
}
