package edu.cit.estrera.wearisit.test.outfit_management_tests;

import edu.cit.estrera.wearisit.features.outfit_management.Outfit;
import edu.cit.estrera.wearisit.features.outfit_management.OutfitRepository;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Outfit Ownership — SecurityUtil.isOutfitOwner")
class OutfitSecurityTest {

    @Mock
    private OutfitRepository outfitRepository;

    @InjectMocks
    private SecurityUtil securityUtil;

    private Outfit outfit;

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setId(1L);

        outfit = new Outfit();
        outfit.setId(100L);
        outfit.setUser(owner);
    }

    @Test
    @DisplayName("returns true when current user owns the outfit")
    void isOutfitOwner_ownerRequests_returnsTrue() {
        try (var mocked = mockStatic(SecurityUtil.class, CALLS_REAL_METHODS)) {

            SecurityUtil util = spy(securityUtil);

            doReturn(1L).when(util).getCurrentUserId();
            when(outfitRepository.findById(100L)).thenReturn(Optional.of(outfit));

            boolean result = util.isOutfitOwner(100L);

            assertThat(result).isTrue();
        }
    }

    @Test
    @DisplayName("returns false when current user does not own the outfit")
    void isOutfitOwner_strangerRequests_returnsFalse() {
        try (var mocked = mockStatic(SecurityUtil.class, CALLS_REAL_METHODS)) {

            SecurityUtil util = spy(securityUtil);

            doReturn(2L).when(util).getCurrentUserId();
            when(outfitRepository.findById(100L)).thenReturn(Optional.of(outfit));

            boolean result = util.isOutfitOwner(100L);

            assertThat(result).isFalse();
        }
    }

    @Test
    @DisplayName("returns false when outfit does not exist")
    void isOutfitOwner_outfitNotFound_returnsFalse() {
        when(outfitRepository.findById(999L)).thenReturn(Optional.empty());

        SecurityUtil util = spy(securityUtil);

        doReturn(1L).when(util).getCurrentUserId();

        boolean result = util.isOutfitOwner(999L);

        assertThat(result).isFalse();
    }
}