package edu.cit.estrera.wearisit.test.clothing_item_management_tests;

import edu.cit.estrera.wearisit.features.clothing_item_management.*;
import edu.cit.estrera.wearisit.features.clothing_item_management.create_item.ClothingItemResponse;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WearClothingItemTest {

    @Mock
    private ClothingItemRepository clothingItemRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private ClothingItemService clothingItemService;

    private User user;
    private ClothingItem item;

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
        item.setItemName("Test Shirt");
        item.setType(type);
        item.setItemWc(5);
        item.setUser(user);
        item.setLastWorn(null);
    }

    @Test
    void wearClothingItem_Success_IncrementsWearCount() {
        LocalDateTime beforeTest = LocalDateTime.now();

        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(clothingItemRepository.findByIdAndUser_Id(1L, 1L))
                .thenReturn(Optional.of(item));
        when(clothingItemRepository.save(any(ClothingItem.class))).thenReturn(item);

        ClothingItemResponse result = clothingItemService.wearClothingItem(1L);

        assertNotNull(result);
        assertEquals(6, item.getItemWc());
        assertNotNull(item.getLastWorn());
        assertTrue(item.getLastWorn().isAfter(beforeTest) || item.getLastWorn().isEqual(beforeTest));
        verify(clothingItemRepository).save(item);
    }

    @Test
    void wearClothingItem_Success_WhenWearCountIsNull() {
        item.setItemWc(null);

        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(clothingItemRepository.findByIdAndUser_Id(1L, 1L))
                .thenReturn(Optional.of(item));
        when(clothingItemRepository.save(any(ClothingItem.class))).thenReturn(item);

        ClothingItemResponse result = clothingItemService.wearClothingItem(1L);

        assertNotNull(result);
        assertEquals(1, item.getItemWc());
        verify(clothingItemRepository).save(item);
    }

    @Test
    void wearClothingItem_ThrowsException_WhenItemNotFound() {
        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(clothingItemRepository.findByIdAndUser_Id(999L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> clothingItemService.wearClothingItem(999L))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    ApiException apiEx = (ApiException) ex;
                    assertEquals(ErrorCode.ITEM_001, apiEx.getErrorCode());
                });

        verify(clothingItemRepository, never()).save(any());
    }

    @Test
    void wearClothingItem_ThrowsException_WhenItemBelongsToAnotherUser() {
        User anotherUser = new User();
        anotherUser.setId(2L);

        when(securityUtil.getCurrentUser()).thenReturn(anotherUser);
        when(clothingItemRepository.findByIdAndUser_Id(1L, 2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> clothingItemService.wearClothingItem(1L))
                .isInstanceOf(ApiException.class);

        verify(clothingItemRepository, never()).save(any());
    }

    @Test
    void wearClothingItem_UpdatesLastWornTimestamp() {
        when(securityUtil.getCurrentUser()).thenReturn(user);
        when(clothingItemRepository.findByIdAndUser_Id(1L, 1L))
                .thenReturn(Optional.of(item));
        when(clothingItemRepository.save(any(ClothingItem.class))).thenReturn(item);

        clothingItemService.wearClothingItem(1L);

        assertNotNull(item.getLastWorn());
        verify(clothingItemRepository).save(item);
    }
}