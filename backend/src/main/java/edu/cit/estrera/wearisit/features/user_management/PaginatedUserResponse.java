package edu.cit.estrera.wearisit.features.user_management;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PaginatedUserResponse {
    private List<AdminUserResponse> users;
    private Pagination pagination;

    @Data
    @Builder
    public static class Pagination {
        private int currentPage;
        private int totalPages;
        private long totalUsers;
        private int limit;
    }
}
