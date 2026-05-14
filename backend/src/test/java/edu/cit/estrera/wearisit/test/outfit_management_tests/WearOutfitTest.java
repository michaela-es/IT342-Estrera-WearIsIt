package edu.cit.estrera.wearisit.test.outfit_management_tests;

import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItem;
import edu.cit.estrera.wearisit.features.outfit_management.*;
import edu.cit.estrera.wearisit.features.outfit_management.get_outfit.OutfitResponse;
import edu.cit.estrera.wearisit.features.outfit_management.mapper.OutfitMapper;
import edu.cit.estrera.wearisit.features.user_management.User;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Wear Outfit")
class WearOutfitTest {

    @Mock private OutfitRepository outfitRepository;
    @Mock private OutfitMapper outfitMapper;
    @Mock private SecurityUtil securityUtil;

    @InjectMocks private OutfitService outfitService;

    private Outfit mockOutfit;
    private ClothingItem item1;
    private ClothingItem item2;

    @BeforeEach
    void setUp() {
        User mockUser = new User();
        mockUser.setId(1L);

        item1 = new ClothingItem();
        item1.setId(10L);

        item2 = new ClothingItem();
        item2.setId(20L);

        OutfitItem oi1 = new OutfitItem();
        oi1.setClothingItem(item1);

        OutfitItem oi2 = new OutfitItem();
        oi2.setClothingItem(item2);

        mockOutfit = new Outfit();
        mockOutfit.setId(100L);
        mockOutfit.setUser(mockUser);
        mockOutfit.setOutfitItems(new ArrayList<>(List.of(oi1, oi2)));
    }

    @Test
    @DisplayName("sets lastWorn on outfit to current time")
    void wearOutfit_setsLastWornOnOutfit() {
        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(outfitRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(mockOutfit));

        ArgumentCaptor<Outfit> captor = ArgumentCaptor.forClass(Outfit.class);
        when(outfitRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
        when(outfitMapper.toResponse(any())).thenReturn(new OutfitResponse());

        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        outfitService.wearOutfit(100L);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertThat(captor.getValue().getLastWorn())
                .isAfter(before)
                .isBefore(after);
    }

    @Test
    @DisplayName("sets lastWorn on every clothing item in the outfit")
    void wearOutfit_propagatesLastWornToAllItems() {
        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(outfitRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(mockOutfit));
        when(outfitRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(outfitMapper.toResponse(any())).thenReturn(new OutfitResponse());

        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        outfitService.wearOutfit(100L);

        assertThat(item1.getLastWorn()).isAfter(before);
        assertThat(item2.getLastWorn()).isAfter(before);
    }

    @Test
    @DisplayName("both outfit and items share the exact same lastWorn timestamp")
    void wearOutfit_outfitAndItemsShareSameTimestamp() {
        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(outfitRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(mockOutfit));

        ArgumentCaptor<Outfit> captor = ArgumentCaptor.forClass(Outfit.class);
        when(outfitRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
        when(outfitMapper.toResponse(any())).thenReturn(new OutfitResponse());

        outfitService.wearOutfit(100L);

        LocalDateTime outfitTs = captor.getValue().getLastWorn();
        assertThat(item1.getLastWorn()).isEqualTo(outfitTs);
        assertThat(item2.getLastWorn()).isEqualTo(outfitTs);
    }

    @Test
    @DisplayName("throws OUTFIT_001 when outfit does not belong to current user")
    void wearOutfit_notOwned_throwsOutfit001() {
        when(securityUtil.getCurrentUserId()).thenReturn(2L);
        when(outfitRepository.findByIdAndUserId(100L, 2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> outfitService.wearOutfit(100L))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.OUTFIT_001));
    }

    @Test
    @DisplayName("saves the outfit exactly once")
    void wearOutfit_savesExactlyOnce() {
        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(outfitRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(mockOutfit));
        when(outfitRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(outfitMapper.toResponse(any())).thenReturn(new OutfitResponse());

        outfitService.wearOutfit(100L);

        verify(outfitRepository, times(1)).save(any(Outfit.class));
    }
}
