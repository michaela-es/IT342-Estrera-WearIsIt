package edu.cit.estrera.wearisit.test.clothing_item_management_tests;

import edu.cit.estrera.wearisit.features.category_management.Category;
import edu.cit.estrera.wearisit.features.category_management.CategoryService;
import edu.cit.estrera.wearisit.features.clothing_item_management.*;
import edu.cit.estrera.wearisit.features.clothing_item_management.create_item.ClothingItemResponse;
import edu.cit.estrera.wearisit.features.tag_management.Tag;
import edu.cit.estrera.wearisit.features.tag_management.TagService;
import edu.cit.estrera.wearisit.features.clothing_item_management.item_type.*;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EditClothingItemTest {

    @Mock
    private ClothingItemRepository clothingItemRepository;

    @Mock
    private ItemTypeRepository itemTypeRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private TagService tagService;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private ClothingItemService clothingItemService;

    private User user;
    private ClothingItem item;
    private EditClothingItemRequest editRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        ItemType type = new ItemType();
        type.setId(100L);
        type.setName("Shirt");

        item = new ClothingItem();
        item.setId(1L);
        item.setItemName("Old Shirt");
        item.setType(type);
        item.setItemWc(0);
        item.setUser(user);
        item.setCategories(new HashSet<>());
        item.setTags(new ArrayList<>());

        editRequest = new EditClothingItemRequest();
    }

    @Test
    void editClothingItem_Success_UpdateNameOnly() {
        editRequest.setItemName("New Shirt Name");

        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(clothingItemRepository.findByIdAndUser_Id(1L, 1L))
                .thenReturn(Optional.of(item));
        when(clothingItemRepository.save(any(ClothingItem.class))).thenReturn(item);

        ClothingItemResponse result = clothingItemService.editClothingItem(1L, editRequest);

        assertNotNull(result);
        verify(clothingItemRepository).save(item);
    }

    @Test
    void editClothingItem_Success_UpdateTypeOnly() {
        ItemType newType = new ItemType();
        newType.setId(200L);
        newType.setName("Pants");

        editRequest.setTypeId(200L);

        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(clothingItemRepository.findByIdAndUser_Id(1L, 1L))
                .thenReturn(Optional.of(item));
        when(itemTypeRepository.findById(200L)).thenReturn(Optional.of(newType));
        when(clothingItemRepository.save(any(ClothingItem.class))).thenReturn(item);

        ClothingItemResponse result = clothingItemService.editClothingItem(1L, editRequest);

        assertNotNull(result);
        verify(itemTypeRepository).findById(200L);
        verify(clothingItemRepository).save(item);
    }

    @Test
    void editClothingItem_Success_UpdateCategoriesOnly() {
        Category category1 = new Category();
        category1.setId(10L);
        category1.setName("Casual");

        Category category2 = new Category();
        category2.setId(20L);
        category2.setName("Summer");

        editRequest.setCategoryIds(List.of(10L, 20L));

        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(clothingItemRepository.findByIdAndUser_Id(1L, 1L))
                .thenReturn(Optional.of(item));
        when(categoryService.getCategoriesByIdsAndUser(List.of(10L, 20L), 1L))
                .thenReturn(List.of(category1, category2));
        when(clothingItemRepository.save(any(ClothingItem.class))).thenReturn(item);

        ClothingItemResponse result = clothingItemService.editClothingItem(1L, editRequest);

        assertNotNull(result);
        verify(categoryService).getCategoriesByIdsAndUser(List.of(10L, 20L), 1L);
        verify(clothingItemRepository).save(item);
    }

    @Test
    void editClothingItem_Success_UpdateTagsOnly() {
        Tag tag1 = new Tag();
        tag1.setId(1L);
        tag1.setName("Cotton");

        Tag tag2 = new Tag();
        tag2.setId(2L);
        tag2.setName("Blue");

        editRequest.setTagIds(List.of(1L, 2L));

        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(clothingItemRepository.findByIdAndUser_Id(1L, 1L))
                .thenReturn(Optional.of(item));
        when(tagService.getTagsByIdsAndUser(List.of(1L, 2L), 1L))
                .thenReturn(List.of(tag1, tag2));
        when(clothingItemRepository.save(any(ClothingItem.class))).thenReturn(item);

        ClothingItemResponse result = clothingItemService.editClothingItem(1L, editRequest);

        assertNotNull(result);
        verify(tagService).getTagsByIdsAndUser(List.of(1L, 2L), 1L);
        verify(clothingItemRepository).save(item);
    }

    @Test
    void editClothingItem_Success_UpdateAllFields() {
        ItemType newType = new ItemType();
        newType.setId(200L);
        newType.setName("Pants");

        Category category = new Category();
        category.setId(10L);
        category.setName("Casual");

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("Cotton");

        editRequest.setItemName("New Pants");
        editRequest.setTypeId(200L);
        editRequest.setCategoryIds(List.of(10L));
        editRequest.setTagIds(List.of(1L));

        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(clothingItemRepository.findByIdAndUser_Id(1L, 1L))
                .thenReturn(Optional.of(item));
        when(itemTypeRepository.findById(200L)).thenReturn(Optional.of(newType));
        when(categoryService.getCategoriesByIdsAndUser(List.of(10L), 1L))
                .thenReturn(List.of(category));
        when(tagService.getTagsByIdsAndUser(List.of(1L), 1L))
                .thenReturn(List.of(tag));
        when(clothingItemRepository.save(any(ClothingItem.class))).thenReturn(item);

        ClothingItemResponse result = clothingItemService.editClothingItem(1L, editRequest);

        assertNotNull(result);
        verify(itemTypeRepository).findById(200L);
        verify(categoryService).getCategoriesByIdsAndUser(List.of(10L), 1L);
        verify(tagService).getTagsByIdsAndUser(List.of(1L), 1L);
        verify(clothingItemRepository).save(item);
    }

    @Test
    void editClothingItem_ThrowsException_WhenItemNotFound() {
        editRequest.setItemName("New Name");

        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(clothingItemRepository.findByIdAndUser_Id(999L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> clothingItemService.editClothingItem(999L, editRequest))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException apiEx = (ApiException) ex;
                    assertEquals(ErrorCode.ITEM_001, apiEx.getErrorCode());
                    assertThat(apiEx.getMessage()).contains("Item not found");
                });

        verify(clothingItemRepository, never()).save(any());
    }

    @Test
    void editClothingItem_ThrowsException_WhenTypeNotFound() {
        editRequest.setTypeId(999L);

        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(clothingItemRepository.findByIdAndUser_Id(1L, 1L))
                .thenReturn(Optional.of(item));
        when(itemTypeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clothingItemService.editClothingItem(1L, editRequest))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException apiEx = (ApiException) ex;
                    assertEquals(ErrorCode.ITEM_003, apiEx.getErrorCode());
                    assertThat(apiEx.getMessage()).contains("Type not found");
                });

        verify(clothingItemRepository, never()).save(any());
    }

    @Test
    void editClothingItem_ThrowsException_WhenCategoriesNotFound() {
        editRequest.setCategoryIds(List.of(10L, 20L));

        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(clothingItemRepository.findByIdAndUser_Id(1L, 1L))
                .thenReturn(Optional.of(item));
        when(categoryService.getCategoriesByIdsAndUser(List.of(10L, 20L), 1L))
                .thenThrow(new ApiException(ErrorCode.CAT_001));

        assertThatThrownBy(() -> clothingItemService.editClothingItem(1L, editRequest))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException apiEx = (ApiException) ex;
                    assertEquals(ErrorCode.CAT_001, apiEx.getErrorCode());
                });

        verify(clothingItemRepository, never()).save(any());
    }

    @Test
    void editClothingItem_ThrowsException_WhenTagsNotFound() {
        editRequest.setTagIds(List.of(1L, 2L));

        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(clothingItemRepository.findByIdAndUser_Id(1L, 1L))
                .thenReturn(Optional.of(item));
        when(tagService.getTagsByIdsAndUser(List.of(1L, 2L), 1L))
                .thenThrow(new ApiException(ErrorCode.ITEM_005));

        assertThatThrownBy(() -> clothingItemService.editClothingItem(1L, editRequest))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException apiEx = (ApiException) ex;
                    assertEquals(ErrorCode.ITEM_005, apiEx.getErrorCode());
                });

        verify(clothingItemRepository, never()).save(any());
    }

    @Test
    void editClothingItem_SkipsUpdate_WhenNullValuesProvided() {
        editRequest.setItemName(null);
        editRequest.setTypeId(null);
        editRequest.setCategoryIds(null);
        editRequest.setTagIds(null);

        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(clothingItemRepository.findByIdAndUser_Id(1L, 1L))
                .thenReturn(Optional.of(item));
        when(clothingItemRepository.save(any(ClothingItem.class))).thenReturn(item);

        ClothingItemResponse result = clothingItemService.editClothingItem(1L, editRequest);

        assertNotNull(result);
        verify(itemTypeRepository, never()).findById(any());
        verify(categoryService, never()).getCategoriesByIdsAndUser(any(), any());
        verify(tagService, never()).getTagsByIdsAndUser(any(), any());
    }

    @Test
    void editClothingItem_ClearsCategories_WhenEmptyListProvided() {
        editRequest.setCategoryIds(new ArrayList<>());

        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(clothingItemRepository.findByIdAndUser_Id(1L, 1L))
                .thenReturn(Optional.of(item));
        when(categoryService.getCategoriesByIdsAndUser(new ArrayList<>(), 1L))
                .thenReturn(new ArrayList<>());
        when(clothingItemRepository.save(any(ClothingItem.class))).thenReturn(item);

        ClothingItemResponse result = clothingItemService.editClothingItem(1L, editRequest);

        assertNotNull(result);
        assertTrue(item.getCategories().isEmpty());
        verify(categoryService).getCategoriesByIdsAndUser(new ArrayList<>(), 1L);
    }

    @Test
    void editClothingItem_ClearsTags_WhenEmptyListProvided() {
        editRequest.setTagIds(new ArrayList<>());

        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(clothingItemRepository.findByIdAndUser_Id(1L, 1L))
                .thenReturn(Optional.of(item));
        when(tagService.getTagsByIdsAndUser(new ArrayList<>(), 1L))
                .thenReturn(new ArrayList<>());
        when(clothingItemRepository.save(any(ClothingItem.class))).thenReturn(item);

        ClothingItemResponse result = clothingItemService.editClothingItem(1L, editRequest);

        assertNotNull(result);
        verify(tagService).getTagsByIdsAndUser(new ArrayList<>(), 1L);
    }
}