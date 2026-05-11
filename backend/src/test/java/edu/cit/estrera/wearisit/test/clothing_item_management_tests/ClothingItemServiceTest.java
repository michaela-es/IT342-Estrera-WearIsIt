package edu.cit.estrera.wearisit.test.clothing_item_management_tests;

import edu.cit.estrera.wearisit.features.category_management.Category;
import edu.cit.estrera.wearisit.features.clothing_item_management.*;
import edu.cit.estrera.wearisit.features.clothing_item_management.create_item.ClothingItemResponse;
import edu.cit.estrera.wearisit.features.clothing_item_management.create_item.CreateClothingItemRequest;
import edu.cit.estrera.wearisit.features.clothing_item_management.item_type.ItemType;
import edu.cit.estrera.wearisit.features.clothing_item_management.item_type.ItemTypeRepository;
import edu.cit.estrera.wearisit.features.tag_management.Tag;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import edu.cit.estrera.wearisit.features.category_management.CategoryService;
import edu.cit.estrera.wearisit.features.tag_management.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClothingItemServiceUnitTest {

    @Mock
    private ClothingItemRepository clothingItemRepository;

    @Mock
    private ItemTypeRepository itemTypeRepository;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private CategoryService categoryService;

    @Mock
    private TagService tagService;

    @InjectMocks
    private ClothingItemService clothingItemService;

    private User testUser;
    private ItemType testType;
    private ClothingItem testItem;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testType = new ItemType();
        testType.setId(1L);
        testType.setName("Top");

        testItem = new ClothingItem();
        testItem.setId(1L);
        testItem.setItemName("White Shirt");
        testItem.setUser(testUser);
    }

    @Test
    void createClothingItem_Success_ShouldSaveItem() {
        CreateClothingItemRequest request = new CreateClothingItemRequest();
        request.setItemName("White Shirt");
        request.setTypeId(1L);

        when(securityUtil.getCurrentUser()).thenReturn(testUser);
        when(itemTypeRepository.findById(1L)).thenReturn(Optional.of(testType));

        when(clothingItemRepository.save(any(ClothingItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ClothingItemResponse response =
                clothingItemService.createClothingItem(request);

        assertNotNull(response);
        assertEquals("White Shirt", response.getItemName());
        assertEquals("Top", response.getTypeName());
    }

    @Test
    void createClothingItem_InvalidType_ShouldThrowITEM003() {
        CreateClothingItemRequest request = new CreateClothingItemRequest();
        request.setTypeId(999L);

        when(securityUtil.getCurrentUser()).thenReturn(testUser);
        when(itemTypeRepository.findById(999L)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> clothingItemService.createClothingItem(request));

        assertEquals(ErrorCode.ITEM_003, ex.getErrorCode());
        verify(clothingItemRepository, never()).save(any());
    }

    @Test
    void createClothingItem_WithCategories_ShouldAttachCategories() {
        CreateClothingItemRequest request = new CreateClothingItemRequest();
        request.setItemName("Shirt");
        request.setTypeId(1L);
        request.setCategoryIds(List.of(1L));

        Category category = new Category();
        category.setId(1L);
        category.setName("Casual");

        when(securityUtil.getCurrentUser()).thenReturn(testUser);
        when(itemTypeRepository.findById(1L)).thenReturn(Optional.of(testType));
        when(categoryService.getCategoriesByIdsAndUser(any(), anyLong()))
                .thenReturn(List.of(category));

        when(clothingItemRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ClothingItemResponse response =
                clothingItemService.createClothingItem(request);

        assertNotNull(response);
    }

    @Test
    void createClothingItem_WithTags_ShouldAttachTags() {
        CreateClothingItemRequest request = new CreateClothingItemRequest();
        request.setItemName("Shirt");
        request.setTypeId(1L);
        request.setTagIds(List.of(1L));

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("Summer");

        when(securityUtil.getCurrentUser()).thenReturn(testUser);
        when(itemTypeRepository.findById(1L)).thenReturn(Optional.of(testType));
        when(tagService.getTagsByIdsAndUser(any(), anyLong()))
                .thenReturn(List.of(tag));

        when(clothingItemRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ClothingItemResponse response =
                clothingItemService.createClothingItem(request);

        assertNotNull(response);
    }


    @Test
    void getClothingItem_Success_ShouldReturnItem() {
        when(securityUtil.getCurrentUser()).thenReturn(testUser);
        when(clothingItemRepository.findByIdAndUser(1L, testUser))
                .thenReturn(Optional.of(testItem));

        ClothingItemResponse response =
                clothingItemService.getClothingItem(1L);

        assertNotNull(response);
        assertEquals("White Shirt", response.getItemName());
    }

    @Test
    void getClothingItem_NotFound_ShouldThrowITEM001() {
        when(securityUtil.getCurrentUser()).thenReturn(testUser);
        when(clothingItemRepository.findByIdAndUser(1L, testUser))
                .thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> clothingItemService.getClothingItem(1L));

        assertEquals(ErrorCode.ITEM_001, ex.getErrorCode());
    }


    @Test
    void getUserClothingItems_ShouldReturnList() {
        when(securityUtil.getCurrentUser()).thenReturn(testUser);
        when(clothingItemRepository.findByUser(testUser))
                .thenReturn(List.of(testItem));

        List<ClothingItemResponse> result =
                clothingItemService.getUserClothingItems();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getUserClothingItems_EmptyList_ShouldReturnEmpty() {
        when(securityUtil.getCurrentUser()).thenReturn(testUser);
        when(clothingItemRepository.findByUser(testUser))
                .thenReturn(List.of());

        List<ClothingItemResponse> result =
                clothingItemService.getUserClothingItems();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}