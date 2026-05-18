package edu.cit.estrera.wearisit.features.user_management;

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
}