package edu.cit.estrera.wearisit.repository;

import edu.cit.estrera.wearisit.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // For single category
    Optional<Category> findByIdAndUser_Id(Long id, Long userId);
    boolean existsByIdAndUser_Id(Long id, Long userId);

    // For multiple categories
    List<Category> findByIdInAndUser_Id(List<Long> ids, Long userId);

    Optional<Category> findByNameAndUser_Id(String name, Long userId);

    List<Category> findByUser_Id(Long userId);
}