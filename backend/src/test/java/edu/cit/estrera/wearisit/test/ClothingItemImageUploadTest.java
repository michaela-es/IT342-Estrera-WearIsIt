package edu.cit.estrera.wearisit.test;

import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import edu.cit.estrera.wearisit.features.image_upload.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ClothingItemImageUploadTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FileStorageService fileStorageService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(fileStorageService, "supabaseUrl", "https://xyz.supabase.co");
        ReflectionTestUtils.setField(fileStorageService, "serviceRoleKey", "test-service-role-key");
        ReflectionTestUtils.setField(fileStorageService, "bucketName", "closet");

        ReflectionTestUtils.setField(fileStorageService, "restTemplate", restTemplate);
    }

    @Test
    void uploadImage_validFile_shouldSucceed() throws IOException {
        String folderPath = "outfits";
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "dummy file content".getBytes()
        );

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("success"));

        String resultUrl = fileStorageService.uploadFile(file, folderPath);

        assertNotNull(resultUrl);
        assertTrue(resultUrl.contains("https://xyz.supabase.co/storage/v1/object/public/closet/outfits/"));
        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void uploadImage_fileTooLarge_shouldThrowFILE001() {
        String folderPath = "outfits";
        byte[] largeContent = new byte[11 * 1024 * 1024];
        MultipartFile file = new MockMultipartFile(
                "file",
                "large-image.png",
                "image/png",
                largeContent
        );

        ApiException ex = assertThrows(ApiException.class, () ->
                fileStorageService.uploadFile(file, folderPath)
        );

        assertEquals(ErrorCode.FILE_001, ex.getErrorCode());
        assertEquals("File size exceeds the maximum limit of 10MB", ex.getMessage());
        verify(restTemplate, never()).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void uploadImage_invalidFileType_shouldThrowFILE002() {
        String folderPath = "outfits";
        MultipartFile file = new MockMultipartFile(
                "file",
                "document.txt",
                "text/plain",
                "dummy file content".getBytes()
        );

        ApiException ex = assertThrows(ApiException.class, () ->
                fileStorageService.uploadFile(file, folderPath)
        );

        assertEquals(ErrorCode.FILE_002, ex.getErrorCode());
        assertEquals("Invalid file type. Only PNG and JPEG images are allowed", ex.getMessage());
        verify(restTemplate, never()).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }
}