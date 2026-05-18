package edu.cit.estrera.wearisit.features.admin;

import edu.cit.estrera.wearisit.features.auth.AuthResponse;
import edu.cit.estrera.wearisit.features.auth.AuthService;
import edu.cit.estrera.wearisit.features.auth.RegisterRequest;
import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItemRepository;
import edu.cit.estrera.wearisit.features.outfit_management.OutfitRepository;
import edu.cit.estrera.wearisit.features.user_management.*;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.security.jwt.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import edu.cit.estrera.wearisit.features.admin.SystemStatsResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository repository;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ClothingItemRepository clothingItemRepository;
    private final OutfitRepository outfitRepository;

    public boolean isEmailWhitelisted(String email) {
        if (email == null) return false;
        return repository.existsByEmailIgnoreCase(email.trim());
    }

    @Transactional
    public AuthResponse registerAdminIfWhitelisted(RegisterRequest request) {
        String email = request.getEmail();

        if (!isEmailWhitelisted(email)) {
            throw new ApiException(ErrorCode.ADMIN_001);
        }

        AuthResponse resp = authService.register(request);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.DB_001));
        user.setRole("ADMIN");
        userRepository.save(user);

        String adminAccess = jwtService.generateAccessToken(user.getUser_id());

        return AuthResponse.builder()
                .accessToken(adminAccess)
                .refreshToken(resp.getRefreshToken())
                .user(resp.getUser())
                .build();
    }

    public PaginatedUserResponse getAllUsers(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<User> userPage = userRepository.findAllValidUsers(pageable);

        List<User> users = userPage.getContent();

        List<Long> userIds = users.stream().map(User::getUser_id).toList();

        Map<Long, Object[]> itemStats = clothingItemRepository.getStatsByUserIds(userIds)
                .stream().collect(Collectors.toMap(arr -> (Long) arr[0], arr -> arr));

        Map<Long, Long> outfitCounts = outfitRepository.getOutfitCountsByUserIds(userIds)
                .stream().collect(Collectors.toMap(arr -> (Long) arr[0], arr -> (Long) arr[1]));

        List<AdminUserResponse> userResponses = users.stream()
                .map(user -> mapToAdminResponse(user, itemStats, outfitCounts))
                .collect(Collectors.toList());

        long totalUsers = userRepository.countValidUsers();
        int totalPages = (int) Math.ceil((double) totalUsers / limit);

        return PaginatedUserResponse.builder()
                .users(userResponses)
                .pagination(PaginatedUserResponse.Pagination.builder()
                        .currentPage(page)
                        .totalPages(totalPages)
                        .totalUsers(totalUsers)
                        .limit(limit)
                        .build())
                .build();
    }

    private AdminUserResponse mapToAdminResponse(User user,
                                                 Map<Long, Object[]> itemStats,
                                                 Map<Long, Long> outfitCounts) {
        Object[] stats = itemStats.get(user.getUser_id());
        int totalItems = stats != null ? ((Long) stats[1]).intValue() : 0;
        int totalWears = stats != null ? ((Long) stats[2]).intValue() : 0;
        int totalOutfits = outfitCounts.getOrDefault(user.getUser_id(), 0L).intValue();

        return AdminUserResponse.builder()
                .id(user.getUser_id())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole() : "USER")
                .provider(user.getProvider() != null ? user.getProvider() : "LOCAL")
                .createdAt(user.getCreatedAt())
                .stats(UserStats.builder()
                        .totalItems(totalItems)
                        .totalOutfits(totalOutfits)
                        .totalWears(totalWears)
                        .lastActive(user.getLastLogin() != null ?
                                user.getLastLogin().toString() : null)
                        .build())
                .build();
    }

    public SystemStatsResponse getSystemStats() {

        SystemStatsProjection p = userRepository.getSystemStats();

        SystemStatsResponse.OverviewStats overview =
                SystemStatsResponse.OverviewStats.builder()
                        .totalUsers(p.getTotalUsers())
                        .totalItems(p.getTotalItems())
                        .totalOutfits(p.getTotalOutfits())
                        .totalWears(p.getTotalWears())
                        .totalCategories(p.getTotalCategories())
                        .totalTags(p.getTotalTags())
                        .build();

        SystemStatsResponse.UserStats userStats =
                SystemStatsResponse.UserStats.builder()
                        .activeToday(p.getActiveToday())
                        .activeThisWeek(p.getActiveWeek())
                        .activeThisMonth(p.getActiveMonth())
                        .newUsersThisMonth(p.getNewUsersMonth())
                        .build();

        SystemStatsResponse.MostActiveUser mostActiveUser = null;

        if (p.getMostActiveUserId() != null) {
            mostActiveUser = SystemStatsResponse.MostActiveUser.builder()
                    .id(p.getMostActiveUserId())
                    .username(p.getMostActiveUsername())
                    .totalWears(p.getMostActiveUserWears())
                    .build();
        }

        SystemStatsResponse.SystemStats systemStats =
                SystemStatsResponse.SystemStats.builder()
                        .averageItemsPerUser(
                                p.getTotalUsers() == 0 ? 0 :
                                        Math.round(((double) p.getTotalItems() / p.getTotalUsers()) * 10) / 10.0
                        )
                        .mostActiveUser(mostActiveUser)
                        .build();

        return SystemStatsResponse.builder()
                .overview(overview)
                .userStats(userStats)
                .systemStats(systemStats)
                .build();
    }
}
