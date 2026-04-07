package edu.cit.estrera.wearisit.service;

import edu.cit.estrera.wearisit.entity.ItemType;
import edu.cit.estrera.wearisit.repository.ItemTypeRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemTypeService {

    private final ItemTypeRepository itemTypeRepository;

    public void validateTypeExists(Long typeId) {
        if (!itemTypeRepository.existsById(typeId)) {
            throw new RuntimeException("Type not found");
        }
    }

    public String getTypeName(Long typeId) {
        return typeId != null ?
                itemTypeRepository.findById(typeId).map(ItemType::getName).orElse(null) : null;
    }

    public boolean existsById(Long typeId) {
        return itemTypeRepository.existsById(typeId);
    }
}