package edu.cit.estrera.wearisit.test.outfit_management_tests;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get Outfit")
class GetOutfitTest {

    @Mock private OutfitRepository outfitRepository;
    @Mock private OutfitMapper outfitMapper;
    @Mock private SecurityUtil securityUtil;

    @InjectMocks private OutfitService outfitService;

    private User mockUser;
    private Outfit mockOutfit;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        mockOutfit = new Outfit();
        mockOutfit.setId(100L);
        mockOutfit.setOutfitName("Summer Fit");
        mockOutfit.setUser(mockUser);
    }

    @Test
    @DisplayName("returns outfit response when user owns the outfit")
    void getOutfit_ownerRequests_returnsResponse() {
        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(outfitRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(mockOutfit));

        OutfitResponse expected = new OutfitResponse();
        expected.setId(100L);
        when(outfitMapper.toResponse(mockOutfit)).thenReturn(expected);

        OutfitResponse result = outfitService.getOutfit(100L);

        assertThat(result.getId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("throws OUTFIT_001 when outfit does not exist for this user")
    void getOutfit_notOwned_throwsOutfit001() {
        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(outfitRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> outfitService.getOutfit(100L))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.OUTFIT_001));
    }

    @Test
    @DisplayName("throws OUTFIT_001 when outfit exists but belongs to another user")
    void getOutfit_wrongUser_throwsOutfit001() {
        when(securityUtil.getCurrentUserId()).thenReturn(2L);
        when(outfitRepository.findByIdAndUserId(100L, 2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> outfitService.getOutfit(100L))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.OUTFIT_001));
    }

    @Test
    @DisplayName("returns all outfits belonging to the current user")
    void getUserOutfits_returnsOnlyUsersOutfits() {
        Outfit outfit2 = new Outfit();
        outfit2.setId(101L);
        outfit2.setUser(mockUser);

        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(outfitRepository.findByUserId(1L))
                .thenReturn(List.of(mockOutfit, outfit2));
        when(outfitMapper.toResponse(any())).thenReturn(new OutfitResponse());

        List<OutfitResponse> results = outfitService.getUserOutfits();

        assertThat(results).hasSize(2);
        verify(outfitRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("returns empty list when user has no outfits")
    void getUserOutfits_noOutfits_returnsEmptyList() {
        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(outfitRepository.findByUserId(1L)).thenReturn(List.of());

        List<OutfitResponse> results = outfitService.getUserOutfits();

        assertThat(results).isEmpty();
    }
}
