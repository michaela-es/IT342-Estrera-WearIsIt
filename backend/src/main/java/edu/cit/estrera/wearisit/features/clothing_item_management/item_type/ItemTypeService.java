package edu.cit.estrera.wearisit.features.clothing_item_management.item_type;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<ItemTypeDto> getAllTypes() {
        List<ItemType> types = itemTypeRepository.findAll();
        return types.stream()
                .map(type -> ItemTypeDto.builder()
                        .id(type.getId())
                        .name(type.getName())
                        .build())
                .collect(Collectors.toList());
    }
    public boolean existsById(Long typeId) {
        return itemTypeRepository.existsById(typeId);
    }
}