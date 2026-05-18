package edu.cit.estrera.wearisit.features.user_management;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminUserResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String provider;
    private LocalDateTime createdAt;
    private UserStats stats;
}
