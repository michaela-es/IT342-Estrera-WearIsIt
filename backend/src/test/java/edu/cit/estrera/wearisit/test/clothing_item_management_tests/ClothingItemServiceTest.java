package edu.cit.estrera.wearisit.test.clothing_item_management_tests;

import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.features.clothing_item_management.create_item.ClothingItemResponse;
import edu.cit.estrera.wearisit.features.clothing_item_management.create_item.CreateClothingItemRequest;
import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItem;
import edu.cit.estrera.wearisit.features.clothing_item_management.item_type.ItemType;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItemRepository;
import edu.cit.estrera.wearisit.features.clothing_item_management.item_type.ItemTypeRepository;
import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItemService;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClothingItemServiceUnitTest {

    @Mock
    private ClothingItemRepository clothingItemRepository;

    @Mock
    private ItemTypeRepository itemTypeRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private ClothingItemService clothingItemService;

    private User testUser;
    private ItemType testType;

    @BeforeEach
    void setup() {
        MockitoAnnotations.
                openMocks(this); // lightweight mock setup

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testType = new ItemType();
        testType.setId(1L);
        testType.setName("Top");
    }

    @Test
    void createClothingItem_Success() {
        // Arrange
        CreateClothingItemRequest request = new CreateClothingItemRequest();
        request.setItemName("White Shirt");
        request.setTypeId(1L);

        when(securityUtil.getCurrentUser()).thenReturn(testUser);
        when(itemTypeRepository.findById(1L)).thenReturn(Optional.of(testType));

        ClothingItem savedItem = new ClothingItem();
        savedItem.setId(1L);
        savedItem.setItemName("White Shirt");
        savedItem.setType(testType);
        savedItem.setUser(testUser);
        savedItem.setItemWc(0);
        savedItem.setCreatedAt(LocalDateTime.now());

        when(clothingItemRepository.save(any(ClothingItem.class))).thenReturn(savedItem);

        // Act
        ClothingItemResponse response = clothingItemService.createClothingItem(request);

        // Assert
        assertNotNull(response);
        assertEquals("White Shirt", response.getItemName());
        assertEquals(1L, response.getTypeId());
        assertEquals("Top", response.getTypeName());
        verify(clothingItemRepository, times(1)).save(any(ClothingItem.class));
    }

    @Test
    void createClothingItem_InvalidType_ShouldThrow() {
        // Arrange
        CreateClothingItemRequest request = new CreateClothingItemRequest();
        request.setItemName("White Shirt");
        request.setTypeId(999L);

        when(securityUtil.getCurrentUser()).thenReturn(testUser);
        when(itemTypeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ApiException ex = assertThrows(ApiException.class,
                () -> clothingItemService.createClothingItem(request));

        assertEquals(ErrorCode.ITEM_003.getCode(), ex.getErrorCode().getCode());
        verify(clothingItemRepository, never()).save(any());
    }
}