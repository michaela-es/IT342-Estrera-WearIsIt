package edu.cit.estrera.wearisit.repository;

import edu.cit.estrera.wearisit.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByNameAndCategory_IdAndUser_Id(String name, Long categoryId, Long userId);

    @Query("SELECT t FROM Tag t WHERE t.id IN :tagIds AND t.user.id = :userId")
    List<Tag> findByIdsAndUserId(@Param("tagIds") List<Long> tagIds, @Param("userId") Long userId);

    Optional<Tag> findByNameAndCategoryIdAndUserId(String tagName, Long id, Long userId);

    List<Tag> findByCategoryIdAndUser_Id(Long id, Long userId);
}