package edu.cit.estrera.wearisit.features.user_management;

import edu.cit.estrera.wearisit.features.admin.SystemStatsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Boolean existsByEmail(String email);

    boolean existsByUsername(String username);
    @Query("SELECT u FROM User u WHERE u.username IS NOT NULL AND u.email IS NOT NULL AND u.email NOT LIKE '%test%'")
    Page<User> findAllValidUsers(Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.username IS NOT NULL AND u.email IS NOT NULL AND u.email NOT LIKE '%test%'")
    long countValidUsers();

    @Query(value = """
    SELECT
        -- OVERVIEW
        (SELECT COUNT(*) FROM users) AS totalUsers,
        (SELECT COUNT(*) FROM items) AS totalItems,
        (SELECT COUNT(*) FROM outfits) AS totalOutfits,
        (SELECT COALESCE(SUM(item_wc), 0) FROM items) AS totalWears,
        (SELECT COUNT(*) FROM categories) AS totalCategories,
        (SELECT COUNT(*) FROM tags) AS totalTags,

        -- USER STATS
        (SELECT COUNT(*) FROM users WHERE last_login >= CURRENT_DATE) AS activeToday,
        (SELECT COUNT(*) FROM users WHERE last_login >= CURRENT_DATE - INTERVAL '7 day') AS activeWeek,
        (SELECT COUNT(*) FROM users WHERE last_login >= CURRENT_DATE - INTERVAL '30 day') AS activeMonth,
        (SELECT COUNT(*) FROM users WHERE created_at >= CURRENT_DATE - INTERVAL '30 day') AS newUsersMonth,

        -- MOST ACTIVE USER (subquery)
        (SELECT u.id
         FROM users u
         LEFT JOIN items i ON i.user_id = u.id
         GROUP BY u.id
         ORDER BY COALESCE(SUM(i.item_wc), 0) DESC
         LIMIT 1) AS mostActiveUserId,

        (SELECT u.username
         FROM users u
         LEFT JOIN items i ON i.user_id = u.id
         GROUP BY u.id, u.username
         ORDER BY COALESCE(SUM(i.item_wc), 0) DESC
         LIMIT 1) AS mostActiveUsername,

        (SELECT COALESCE(SUM(i.item_wc), 0)
         FROM users u
         LEFT JOIN items i ON i.user_id = u.id
         GROUP BY u.id
         ORDER BY COALESCE(SUM(i.item_wc), 0) DESC
         LIMIT 1) AS mostActiveUserWears

    """, nativeQuery = true)
    SystemStatsProjection getSystemStats();
}