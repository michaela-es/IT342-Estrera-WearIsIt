package edu.cit.estrera.wearisit.features.admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<AdminWhitelist, Long> {
    boolean existsByEmailIgnoreCase(String email);
}

