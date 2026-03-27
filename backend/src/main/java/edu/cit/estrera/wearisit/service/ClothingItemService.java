package edu.cit.estrera.wearisit.service;

import edu.cit.estrera.wearisit.api.ApiException;
import edu.cit.estrera.wearisit.api.ErrorCode;
import edu.cit.estrera.wearisit.dto.CategoryDto;
import edu.cit.estrera.wearisit.dto.ClothingItemResponse;
import edu.cit.estrera.wearisit.dto.CreateClothingItemRequest;
import edu.cit.estrera.wearisit.dto.TagDto;
import edu.cit.estrera.wearisit.entity.*;
import edu.cit.estrera.wearisit.repository.CategoryRepository;
import edu.cit.estrera.wearisit.repository.ClothingItemRepository;
import edu.cit.estrera.wearisit.repository.ItemTypeRepository;
import edu.cit.estrera.wearisit.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClothingItemService {

    private final ClothingItemRepository clothingItemRepository;
    private final SecurityUtil securityUtil;

    private final ItemTypeService itemTypeService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final ItemTypeRepository itemTypeRepository;
    private final CategoryRepository categoryRepository;
    @Transactional
    public ClothingItemResponse createClothingItem(CreateClothingItemRequest request) {
        // Get current user
        User currentUser = securityUtil.getCurrentUser();
        Long userId = currentUser.getUser_id();

        // Get type
        ItemType type = itemTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new ApiException(ErrorCode.ITEM_003));

        // Create item
        ClothingItem item = new ClothingItem();
        item.setItemName(request.getItemName());
        item.setType(type);
        item.setItemWc(0);
        item.setUser(currentUser);

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryService.getCategoriesByIdsAndUser(request.getCategoryIds(), userId);
            item.getCategories().addAll(categories);
        }

        // Handle existing tags (using TagService)
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> existingTags = tagService.getTagsByIdsAndUser(request.getTagIds(), userId);
            item.getTags().addAll(existingTags);
        }

        // Handle new properties (using TagService to create on the fly)
        if (request.getProperties() != null && !request.getProperties().isEmpty()) {
            List<Tag> newTags = processProperties(request.getProperties(), currentUser, userId);
            item.getTags().addAll(newTags);
        }

        ClothingItem savedItem = clothingItemRepository.save(item);
        return mapToResponse(savedItem);
    }

    private List<Tag> processProperties(Map<String, String> properties, User user, Long userId) {
        return properties.entrySet().stream()
                .map(entry -> tagService.getOrCreateTag(entry.getKey(), entry.getValue(), user, userId))
                .collect(Collectors.toList());
    }

    private Map<String, List<String>> buildProperties(List<Tag> tags) {
        Map<String, List<String>> properties = new HashMap<>();
        for (Tag tag : tags) {
            if (tag.getCategory() != null) {
                String categoryName = tag.getCategory().getName();
                properties.computeIfAbsent(categoryName, k -> new ArrayList<>()).add(tag.getName());
            }
        }
        return properties;
    }
//    private ClothingItemResponse mapToResponse(ClothingItem item) {
//        return ClothingItemResponse.builder()
//                .id(item.getId())
//                .itemName(item.getItemName())
//                .imageUrl(item.getImageUrl())
//                .itemWc(item.getItemWc())
//                .typeId(item.getType() != null ? item.getType().getId() : null)
//                .typeName(item.getType() != null ? item.getType().getName() : null)
//                .properties(buildProperties(item.getTags()))
//                .lastWorn(item.getLastWorn())
//                .createdAt(item.getCreatedAt())
//                .build();
//    }

    @Transactional(readOnly = true)
    public ClothingItemResponse getClothingItem(Long id) {
        User currentUser = securityUtil.getCurrentUser();

        ClothingItem item = clothingItemRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ApiException(ErrorCode.ITEM_001));

        return mapToResponse(item);
    }

    @Transactional(readOnly = true)
    public List<ClothingItemResponse> getUserClothingItems() {
        User currentUser = securityUtil.getCurrentUser();

        List<ClothingItem> items = clothingItemRepository.findByUser(currentUser);

        return items.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ClothingItemResponse mapToResponse(ClothingItem item) {
        Map<Long, Category> categoryMap = item.getTags().stream()
                .map(Tag::getCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Category::getId, Function.identity(), (a, b) -> a));

        List<CategoryDto> categories = categoryMap.values().stream()
                .map(c -> CategoryDto.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .build())
                .collect(Collectors.toList());

        List<TagDto> tags = item.getTags().stream()
                .map(t -> TagDto.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .categoryId(t.getCategory() != null ? t.getCategory().getId() : null)
                        .categoryName(t.getCategory() != null ? t.getCategory().getName() : null)
                        .build())
                .collect(Collectors.toList());

        return ClothingItemResponse.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .imageUrl(item.getImageUrl())
                .itemWc(item.getItemWc())
                .typeId(item.getType() != null ? item.getType().getId() : null)
                .typeName(item.getType() != null ? item.getType().getName() : null)
                .categories(categories)
                .tags(tags)
                .lastWorn(item.getLastWorn())
                .createdAt(item.getCreatedAt())
                .build();
    }
}