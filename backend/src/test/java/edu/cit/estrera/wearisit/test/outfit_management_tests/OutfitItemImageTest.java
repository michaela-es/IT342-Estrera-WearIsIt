package edu.cit.estrera.wearisit.test.outfit_management_tests;

import edu.cit.estrera.wearisit.features.image_upload.FileStorageService;
import edu.cit.estrera.wearisit.features.outfit_management.Outfit;
import edu.cit.estrera.wearisit.features.outfit_management.OutfitRepository;
import edu.cit.estrera.wearisit.features.user_management.User;
import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.infrastructure.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import edu.cit.estrera.wearisit.features.outfit_management.add_image.OutfitImageService;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutfitImageServiceTest {

    @Mock
    private OutfitRepository outfitRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private OutfitImageService outfitImageService;

    private User mockUser;
    private Outfit mockOutfit;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");

        mockOutfit = new Outfit();
        mockOutfit.setId(100L);
        mockOutfit.setOutfitName("Test Outfit");
        mockOutfit.setUser(mockUser);
        mockOutfit.setCoverImageUrl(null);

        mockFile = new MockMultipartFile(
                "image",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
    }

    @Test
    void saveOutfitImage_Success() throws IOException {
        Long outfitId = 100L;
        String expectedImageUrl = "https://supabase.com/storage/v1/object/public/closet/users/secret/outfits/image.jpg";

        when(securityUtil.getCurrentUser()).thenReturn(mockUser);
        when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(mockOutfit));
        when(fileStorageService.uploadFile(any(MultipartFile.class), anyString()))
                .thenReturn(expectedImageUrl);

        String result = outfitImageService.saveOutfitImage(outfitId, mockFile);

        assertThat(result).isEqualTo(expectedImageUrl);
        assertThat(mockOutfit.getCoverImageUrl()).isEqualTo(expectedImageUrl);
        verify(outfitRepository).save(mockOutfit);
        verify(fileStorageService).uploadFile(eq(mockFile), anyString());
    }

    @Test
    void saveOutfitImage_OutfitNotFound_ThrowsException() throws IOException {
        Long outfitId = 999L;
        when(outfitRepository.findById(outfitId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> outfitImageService.saveOutfitImage(outfitId, mockFile))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.OUTFIT_001);

        verify(outfitRepository, never()).save(any());
        verify(fileStorageService, never()).uploadFile(any(), anyString());
    }

    @Test
    void saveOutfitImage_UserNotOwner_ThrowsException() throws IOException {
        Long outfitId = 100L;
        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setEmail("other@example.com");

        mockOutfit.setUser(differentUser);

        when(securityUtil.getCurrentUser()).thenReturn(mockUser);
        when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(mockOutfit));

        assertThatThrownBy(() -> outfitImageService.saveOutfitImage(outfitId, mockFile))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.OUTFIT_002);

        verify(outfitRepository, never()).save(any());
        verify(fileStorageService, never()).uploadFile(any(), anyString());
    }

    @Test
    void saveOutfitImage_UpdatesExistingImageUrl() throws IOException {
        Long outfitId = 100L;
        String oldImageUrl = "https://old-image.com/outfit.jpg";
        String newImageUrl = "https://supabase.com/new-image.jpg";

        mockOutfit.setCoverImageUrl(oldImageUrl);

        when(securityUtil.getCurrentUser()).thenReturn(mockUser);
        when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(mockOutfit));
        when(fileStorageService.uploadFile(any(MultipartFile.class), anyString()))
                .thenReturn(newImageUrl);

        String result = outfitImageService.saveOutfitImage(outfitId, mockFile);

        assertThat(result).isEqualTo(newImageUrl);
        assertThat(mockOutfit.getCoverImageUrl()).isEqualTo(newImageUrl);
        assertThat(mockOutfit.getCoverImageUrl()).isNotEqualTo(oldImageUrl);
        verify(outfitRepository).save(mockOutfit);
    }

    @Test
    void saveOutfitImage_UploadFails_ThrowsException() throws IOException {
        Long outfitId = 100L;

        when(securityUtil.getCurrentUser()).thenReturn(mockUser);
        when(outfitRepository.findById(outfitId)).thenReturn(Optional.of(mockOutfit));
        when(fileStorageService.uploadFile(any(MultipartFile.class), anyString()))
                .thenThrow(new IOException("Upload failed"));

        assertThatThrownBy(() -> outfitImageService.saveOutfitImage(outfitId, mockFile))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Upload failed");

        verify(outfitRepository, never()).save(any());
    }
}