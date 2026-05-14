package edu.cit.estrera.wearisit.test.outfit_management_tests;

import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItem;
import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItemRepository;
import edu.cit.estrera.wearisit.features.clothing_item_management.item_type.ItemType;
import edu.cit.estrera.wearisit.features.outfit_management.*;
import edu.cit.estrera.wearisit.features.outfit_management.create_outfit.CreateOutfitItemRequest;
import edu.cit.estrera.wearisit.features.outfit_management.mapper.OutfitMapper;
import edu.cit.estrera.wearisit.features.outfit_management.validate_outfit.OutfitValidationResponse;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.features.user_management.UserRepository;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Validate Outfit Composition")
class ValidateOutfitTest {

    @Mock private ClothingItemRepository clothingItemRepository;

    @InjectMocks private OutfitService outfitService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
    }

    @Test
    @DisplayName("valid: 1 top + 1 bottom")
    void validate_topAndBottom_isValid() {
        List<ClothingItem> items = List.of(
                buildItem(1L, "TOP"),
                buildItem(2L, "BOTTOM")
        );
        when(clothingItemRepository.findAllById(anyList())).thenReturn(items);

        OutfitValidationResponse result = outfitService.validateComposition(dtos(1L, 2L));

        assertThat(result.isValid()).isTrue();
        assertThat(result.getViolations()).isEmpty();
    }

    @Test
    @DisplayName("valid: 1 top + 1 bottom + 1 shoes + 2 accessories")
    void validate_fullOutfit_isValid() {
        List<ClothingItem> items = List.of(
                buildItem(1L, "TOP"),
                buildItem(2L, "BOTTOM"),
                buildItem(3L, "SHOES"),
                buildItem(4L, "ACCESSORY"),
                buildItem(5L, "ACCESSORY")
        );
        when(clothingItemRepository.findAllById(anyList())).thenReturn(items);

        OutfitValidationResponse result = outfitService.validateComposition(dtos(1L, 2L, 3L, 4L, 5L));

        assertThat(result.isValid()).isTrue();
    }

    @Test
    @DisplayName("valid: exactly 5 accessories allowed")
    void validate_fiveAccessories_isValid() {
        List<ClothingItem> items = List.of(
                buildItem(1L, "TOP"),
                buildItem(2L, "ACCESSORY"),
                buildItem(3L, "ACCESSORY"),
                buildItem(4L, "ACCESSORY"),
                buildItem(5L, "ACCESSORY"),
                buildItem(6L, "ACCESSORY")
        );
        when(clothingItemRepository.findAllById(anyList())).thenReturn(items);

        OutfitValidationResponse result = outfitService.validateComposition(dtos(1L, 2L, 3L, 4L, 5L, 6L));

        assertThat(result.isValid()).isTrue();
    }

    @Test
    @DisplayName("invalid: 2 TOP items exceeds max of 1")
    void validate_twoTops_isInvalid() {
        List<ClothingItem> items = List.of(
                buildItem(1L, "TOP"),
                buildItem(2L, "TOP")
        );
        when(clothingItemRepository.findAllById(anyList())).thenReturn(items);

        OutfitValidationResponse result = outfitService.validateComposition(dtos(1L, 2L));

        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations())
                .anyMatch(v -> v.contains("TOP") && v.contains("1"));
    }

    @Test
    @DisplayName("invalid: 2 BOTTOM items exceeds max of 1")
    void validate_twoBottoms_isInvalid() {
        List<ClothingItem> items = List.of(
                buildItem(1L, "BOTTOM"),
                buildItem(2L, "BOTTOM")
        );
        when(clothingItemRepository.findAllById(anyList())).thenReturn(items);

        OutfitValidationResponse result = outfitService.validateComposition(dtos(1L, 2L));

        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations())
                .anyMatch(v -> v.contains("BOTTOM"));
    }

    @Test
    @DisplayName("invalid: 2 SHOES items exceeds max of 1")
    void validate_twoShoes_isInvalid() {
        List<ClothingItem> items = List.of(
                buildItem(1L, "SHOES"),
                buildItem(2L, "SHOES")
        );
        when(clothingItemRepository.findAllById(anyList())).thenReturn(items);

        OutfitValidationResponse result = outfitService.validateComposition(dtos(1L, 2L));

        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations())
                .anyMatch(v -> v.contains("SHOES"));
    }

    @Test
    @DisplayName("invalid: 6 ACCESSORY items exceeds max of 5")
    void validate_sixAccessories_isInvalid() {
        List<ClothingItem> items = List.of(
                buildItem(1L, "ACCESSORY"),
                buildItem(2L, "ACCESSORY"),
                buildItem(3L, "ACCESSORY"),
                buildItem(4L, "ACCESSORY"),
                buildItem(5L, "ACCESSORY"),
                buildItem(6L, "ACCESSORY")
        );
        when(clothingItemRepository.findAllById(anyList())).thenReturn(items);

        OutfitValidationResponse result = outfitService.validateComposition(
                dtos(1L, 2L, 3L, 4L, 5L, 6L));

        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations())
                .anyMatch(v -> v.contains("ACCESSORY") && v.contains("5"));
    }

    @Test
    @DisplayName("invalid: unknown item type produces violation message")
    void validate_unknownType_isInvalid() {
        List<ClothingItem> items = List.of(
                buildItem(1L, "TOP"),
                buildItem(2L, "JUMPSUIT")
        );
        when(clothingItemRepository.findAllById(anyList())).thenReturn(items);

        OutfitValidationResponse result = outfitService.validateComposition(dtos(1L, 2L));

        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations())
                .anyMatch(v -> v.contains("JUMPSUIT"));
    }

    @Test
    @DisplayName("invalid: null items list returns false immediately")
    void validate_nullItems_isInvalid() {
        OutfitValidationResponse result = outfitService.validateComposition(null);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).isNotEmpty();
        verifyNoInteractions(clothingItemRepository);
    }

    @Test
    @DisplayName("invalid: empty items list returns false immediately")
    void validate_emptyItems_isInvalid() {
        OutfitValidationResponse result = outfitService.validateComposition(Collections.emptyList());

        assertThat(result.isValid()).isFalse();
        verifyNoInteractions(clothingItemRepository);
    }

    @Test
    @DisplayName("invalid: duplicate item IDs in request")
    void validate_duplicateItemIds_isInvalid() {
        List<ClothingItem> items = List.of(
                buildItem(1L, "TOP"),
                buildItem(2L, "BOTTOM")
        );
        when(clothingItemRepository.findAllById(anyList())).thenReturn(items);

        List<CreateOutfitItemRequest> duplicateDtos = List.of(
                dto(1L), dto(1L)
        );

        OutfitValidationResponse result = outfitService.validateComposition(duplicateDtos);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations())
                .anyMatch(v -> v.toLowerCase().contains("duplicate"));
    }

    @Test
    @DisplayName("invalid: one or more item IDs not found in database")
    void validate_itemsNotFound_isInvalid() {
        when(clothingItemRepository.findAllById(anyList()))
                .thenReturn(List.of(buildItem(1L, "TOP")));

        OutfitValidationResponse result = outfitService.validateComposition(dtos(1L, 999L));

        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations())
                .anyMatch(v -> v.toLowerCase().contains("not found"));
    }

    @Test
    @DisplayName("accumulates multiple violations in one response")
    void validate_multipleViolations_allReported() {
        List<ClothingItem> items = List.of(
                buildItem(1L, "TOP"),
                buildItem(2L, "TOP"),
                buildItem(3L, "BOTTOM"),
                buildItem(4L, "BOTTOM")
        );
        when(clothingItemRepository.findAllById(anyList())).thenReturn(items);

        OutfitValidationResponse result = outfitService.validateComposition(dtos(1L, 2L, 3L, 4L));

        assertThat(result.isValid()).isFalse();
        assertThat(result.getViolations()).hasSizeGreaterThanOrEqualTo(2);
    }


    private ClothingItem buildItem(Long id, String typeName) {
        ItemType type = new ItemType();
        switch (typeName) {
            case "TOP" -> type.setId(100L);
            case "BOTTOM" -> type.setId(200L);
            case "SHOES" -> type.setId(300L);
            case "ACCESSORY" -> type.setId(400L);
            default -> type.setId(999L);
        }
        type.setName(typeName);

        ClothingItem item = new ClothingItem();
        item.setId(id);
        item.setUser(mockUser);
        item.setType(type);
        return item;
    }

    private List<CreateOutfitItemRequest> dtos(Long... ids) {
        List<CreateOutfitItemRequest> list = new ArrayList<>();
        int pos = 0;
        for (Long id : ids) {
            list.add(dto(id, pos++));
        }
        return list;
    }

    private CreateOutfitItemRequest dto(Long id) {
        return dto(id, 0);
    }

    private CreateOutfitItemRequest dto(Long id, int position) {
        CreateOutfitItemRequest dto = new CreateOutfitItemRequest();
        dto.setItemId(id);
        dto.setPosition(position);
        return dto;
    }
}
