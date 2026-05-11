package edu.cit.estrera.wearisit.features.clothing_item_management;

import edu.cit.estrera.wearisit.features.category_management.Category;
import edu.cit.estrera.wearisit.features.category_management.CategoryService;
import edu.cit.estrera.wearisit.features.clothing_item_management.item_type.ItemType;
import edu.cit.estrera.wearisit.features.clothing_item_management.item_type.ItemTypeRepository;
import edu.cit.estrera.wearisit.features.tag_management.Tag;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.features.category_management.CategoryDto;
import edu.cit.estrera.wearisit.features.clothing_item_management.create_item.ClothingItemResponse;
import edu.cit.estrera.wearisit.features.clothing_item_management.create_item.CreateClothingItemRequest;
import edu.cit.estrera.wearisit.features.tag_management.TagDto;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import edu.cit.estrera.wearisit.features.tag_management.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClothingItemService {

    private final ClothingItemRepository clothingItemRepository;
    private final SecurityUtil securityUtil;
    private final ItemTypeRepository itemTypeRepository;
    private final CategoryService categoryService;
    private final TagService tagService;

    @Transactional
    public ClothingItemResponse createClothingItem(CreateClothingItemRequest request) {
        User currentUser = securityUtil.getCurrentUser();
        Long userId = currentUser.getUser_id();

        ItemType type = itemTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new ApiException(ErrorCode.ITEM_003));

        ClothingItem item = new ClothingItem();
        item.setItemName(request.getItemName());
        item.setType(type);
        item.setItemWc(0);
        item.setUser(currentUser);

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryService.getCategoriesByIdsAndUser(request.getCategoryIds(), userId);
            if (categories != null && !categories.isEmpty()) {
                item.getCategories().addAll(new HashSet<>(categories));
            }
        }

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> tags = tagService.getTagsByIdsAndUser(request.getTagIds(), userId);
            if (tags != null && !tags.isEmpty()) {
                item.getTags().addAll(new ArrayList<>(tags));
            }
        }

        ClothingItem savedItem = clothingItemRepository.save(item);

        return convertToResponse(savedItem);
    }

    @Transactional(readOnly = true)
    public ClothingItemResponse getClothingItem(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        ClothingItem item = clothingItemRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ApiException(ErrorCode.ITEM_001));

        return convertToResponse(item);
    }

    @Transactional(readOnly = true)
    public List<ClothingItemResponse> getUserClothingItems() {
        User currentUser = securityUtil.getCurrentUser();
        List<ClothingItem> items = clothingItemRepository.findByUser(currentUser);

        return items.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private ClothingItemResponse convertToResponse(ClothingItem item) {
        List<CategoryDto> categories = new ArrayList<>();
        if (item.getCategories() != null) {
            for (Category category : item.getCategories()) {
                categories.add(CategoryDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build());
            }
        }

        List<TagDto> tags = new ArrayList<>();
        if (item.getTags() != null) {
            for (Tag tag : item.getTags()) {
                tags.add(TagDto.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .categoryId(tag.getCategory() != null ? tag.getCategory().getId() : null)
                        .categoryName(tag.getCategory() != null ? tag.getCategory().getName() : null)
                        .build());
            }
        }

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