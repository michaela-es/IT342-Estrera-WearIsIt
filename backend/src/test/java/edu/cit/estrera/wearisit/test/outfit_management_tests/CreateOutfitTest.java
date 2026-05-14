package edu.cit.estrera.wearisit.test.outfit_management_tests;

import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItem;
import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItemRepository;
import edu.cit.estrera.wearisit.features.clothing_item_management.item_type.ItemType;
import edu.cit.estrera.wearisit.features.outfit_management.Outfit;
import edu.cit.estrera.wearisit.features.outfit_management.OutfitRepository;
import edu.cit.estrera.wearisit.features.outfit_management.OutfitItemRepository;
import edu.cit.estrera.wearisit.features.outfit_management.OutfitService;
import edu.cit.estrera.wearisit.features.outfit_management.create_outfit.CreateOutfitItemRequest;
import edu.cit.estrera.wearisit.features.outfit_management.create_outfit.CreateOutfitRequest;
import edu.cit.estrera.wearisit.features.outfit_management.get_outfit.OutfitResponse;
import edu.cit.estrera.wearisit.features.outfit_management.mapper.OutfitMapper;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.features.user_management.UserRepository;
import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Create Outfit")
class CreateOutfitTest {

    @Mock private OutfitRepository outfitRepository;
    @Mock private OutfitItemRepository outfitItemRepository;
    @Mock private ClothingItemRepository clothingItemRepository;
    @Mock private UserRepository userRepository;
    @Mock private OutfitMapper outfitMapper;
    @Mock private SecurityUtil securityUtil;

    @InjectMocks private OutfitService outfitService;

    private User mockUser;
    private ClothingItem mockTop;
    private ClothingItem mockBottom;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        ItemType topType = new ItemType();
        topType.setId(1L);
        topType.setName("TOP");

        ItemType bottomType = new ItemType();
        bottomType.setId(2L);
        bottomType.setName("BOTTOM");

        mockTop = new ClothingItem();
        mockTop.setId(10L);
        mockTop.setItemName("White Tee");
        mockTop.setItemWc(3);
        mockTop.setUser(mockUser);
        mockTop.setType(topType);

        mockBottom = new ClothingItem();
        mockBottom.setId(20L);
        mockBottom.setItemName("Blue Jeans");
        mockBottom.setItemWc(5);
        mockBottom.setUser(mockUser);
        mockBottom.setType(bottomType);
    }

    @Test
    @DisplayName("creates outfit and returns response when composition is valid")
    void createOutfit_validRequest_returnsResponse() {
        CreateOutfitRequest request = buildRequest("Summer Fit",
                itemDto(10L, 0, null),
                itemDto(20L, 1, null));

        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(clothingItemRepository.findAllById(anyList()))
                .thenReturn(List.of(mockTop, mockBottom));
        when(clothingItemRepository.findById(10L)).thenReturn(Optional.of(mockTop));
        when(clothingItemRepository.findById(20L)).thenReturn(Optional.of(mockBottom));

        Outfit savedOutfit = new Outfit();
        savedOutfit.setId(100L);
        when(outfitRepository.save(any(Outfit.class))).thenReturn(savedOutfit);

        OutfitResponse mockResponse = new OutfitResponse();
        mockResponse.setId(100L);
        when(outfitMapper.toResponse(savedOutfit)).thenReturn(mockResponse);

        OutfitResponse result = outfitService.createOutfit(request);

        assertThat(result.getId()).isEqualTo(100L);
        verify(outfitRepository).save(any(Outfit.class));
    }

    @Test
    @DisplayName("calculates outfit WC as sum of all item WCs")
    void createOutfit_calculatesWcCorrectly() {
        // mockTop.wc=3, mockBottom.wc=5 → expected total = 8
        CreateOutfitRequest request = buildRequest("WC Test",
                itemDto(10L, 0, null),
                itemDto(20L, 1, null));

        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(clothingItemRepository.findAllById(anyList()))
                .thenReturn(List.of(mockTop, mockBottom));
        when(clothingItemRepository.findById(10L)).thenReturn(Optional.of(mockTop));
        when(clothingItemRepository.findById(20L)).thenReturn(Optional.of(mockBottom));

        ArgumentCaptor<Outfit> captor = ArgumentCaptor.forClass(Outfit.class);
        when(outfitRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
        when(outfitMapper.toResponse(any())).thenReturn(new OutfitResponse());

        outfitService.createOutfit(request);

        assertThat(captor.getValue().getOutfitWc()).isEqualTo(8);
    }

    @Test
    @DisplayName("assigns auto-incrementing position when position is null")
    void createOutfit_nullPositions_assignedByIndex() {
        CreateOutfitRequest request = buildRequest("Auto Position",
                itemDto(10L, null, null),
                itemDto(20L, null, null));

        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(clothingItemRepository.findAllById(anyList()))
                .thenReturn(List.of(mockTop, mockBottom));
        when(clothingItemRepository.findById(10L)).thenReturn(Optional.of(mockTop));
        when(clothingItemRepository.findById(20L)).thenReturn(Optional.of(mockBottom));

        ArgumentCaptor<Outfit> captor = ArgumentCaptor.forClass(Outfit.class);
        when(outfitRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
        when(outfitMapper.toResponse(any())).thenReturn(new OutfitResponse());

        outfitService.createOutfit(request);

        var items = captor.getValue().getOutfitItems();
        assertThat(items.get(0).getPosition()).isEqualTo(0);
        assertThat(items.get(1).getPosition()).isEqualTo(1);
    }

    @Test
    @DisplayName("stores provided notes on each outfit item")
    void createOutfit_notesProvided_persistedOnItems() {
        CreateOutfitRequest request = buildRequest("Notes Test",
                itemDto(10L, 0, "tuck it in"),
                itemDto(20L, 1, null));

        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(clothingItemRepository.findAllById(anyList()))
                .thenReturn(List.of(mockTop, mockBottom));
        when(clothingItemRepository.findById(10L)).thenReturn(Optional.of(mockTop));
        when(clothingItemRepository.findById(20L)).thenReturn(Optional.of(mockBottom));

        ArgumentCaptor<Outfit> captor = ArgumentCaptor.forClass(Outfit.class);
        when(outfitRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
        when(outfitMapper.toResponse(any())).thenReturn(new OutfitResponse());

        outfitService.createOutfit(request);

        assertThat(captor.getValue().getOutfitItems().get(0).getNotes()).isEqualTo("tuck it in");
        assertThat(captor.getValue().getOutfitItems().get(1).getNotes()).isNull();
    }


    @Test
    @DisplayName("throws OUTFIT_003 when composition is invalid")
    void createOutfit_invalidComposition_throwsOutfit003() {
        // Two TOP items — invalid
        ItemType topType = mockTop.getType();
        ClothingItem anotherTop = new ClothingItem();
        anotherTop.setId(11L);
        anotherTop.setItemName("Black Tee");
        anotherTop.setItemWc(2);
        anotherTop.setUser(mockUser);
        anotherTop.setType(topType);

        CreateOutfitRequest request = buildRequest("Bad Outfit",
                itemDto(10L, 0, null),
                itemDto(11L, 1, null));

        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(clothingItemRepository.findAllById(anyList()))
                .thenReturn(List.of(mockTop, anotherTop));

        assertThatThrownBy(() -> outfitService.createOutfit(request))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.OUTFIT_003));
    }

    @Test
    @DisplayName("throws AUTH_005 when user not found")
    void createOutfit_userNotFound_throwsAuth005() {

        when(securityUtil.getCurrentUserId()).thenReturn(99L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        CreateOutfitRequest request = buildRequest(
                "Ghost User",
                itemDto(10L, 0, null),
                itemDto(20L, 1, null)
        );

        assertThatThrownBy(() -> outfitService.createOutfit(request))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.AUTH_005));
    }

    @Test
    @DisplayName("throws OUTFIT_005 when item belongs to a different user")
    void createOutfit_itemOwnedByOtherUser_throwsOutfit005() {
        User otherUser = new User();
        otherUser.setId(999L);
        mockBottom.setUser(otherUser);

        CreateOutfitRequest request = buildRequest("Stolen Fit",
                itemDto(10L, 0, null),
                itemDto(20L, 1, null));

        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(clothingItemRepository.findAllById(anyList()))
                .thenReturn(List.of(mockTop, mockBottom));
        when(clothingItemRepository.findById(10L)).thenReturn(Optional.of(mockTop));
        when(clothingItemRepository.findById(20L)).thenReturn(Optional.of(mockBottom));

        assertThatThrownBy(() -> outfitService.createOutfit(request))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.OUTFIT_005));
    }

    @Test
    @DisplayName("throws ITEM_001 when a clothing item ID does not exist")
    void createOutfit_itemNotFound_throwsItem001() {
        CreateOutfitRequest request = buildRequest("Missing Item",
                itemDto(10L, 0, null),
                itemDto(999L, 1, null));

        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(clothingItemRepository.findAllById(anyList()))
                .thenReturn(List.of(mockTop, mockBottom)); // validation passes (returns 2 items)
        when(clothingItemRepository.findById(10L)).thenReturn(Optional.of(mockTop));
        when(clothingItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> outfitService.createOutfit(request))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.ITEM_001));
    }

    private CreateOutfitRequest buildRequest(String name, CreateOutfitItemRequest... items) {
        CreateOutfitRequest req = new CreateOutfitRequest();
        req.setOutfitName(name);
        req.setItems(List.of(items));
        return req;
    }

    private CreateOutfitItemRequest itemDto(Long itemId, Integer position, String notes) {
        CreateOutfitItemRequest dto = new CreateOutfitItemRequest();
        dto.setItemId(itemId);
        dto.setPosition(position);
        dto.setNotes(notes);
        return dto;
    }
}