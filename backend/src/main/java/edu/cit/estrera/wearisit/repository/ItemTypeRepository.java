package edu.cit.estrera.wearisit.repository;

import edu.cit.estrera.wearisit.entity.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ItemTypeRepository extends JpaRepository<ItemType, Long> {

    Optional<ItemType> findByName(String name);

    boolean existsByName(String name);
}