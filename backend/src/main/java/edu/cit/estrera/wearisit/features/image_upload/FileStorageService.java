package edu.cit.estrera.wearisit.features.image_upload;

import edu.cit.estrera.wearisit.infrastructure.api.exceptions.ApiException;
import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String serviceRoleKey;

    @Value("${supabase.bucket:closet}")
    private String bucketName;

    private final RestTemplate restTemplate = new RestTemplate();

    public String uploadFile(MultipartFile file, String folderPath) throws IOException {

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new ApiException(ErrorCode.FILE_001);
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("image/png") && !contentType.equals("image/jpeg"))) {
            throw new ApiException(ErrorCode.FILE_002);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String fullPath = folderPath + "/" + fileName;

        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fullPath;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + serviceRoleKey);
        headers.set("apikey", serviceRoleKey);
        headers.setContentType(MediaType.parseMediaType(contentType));

        HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);

        restTemplate.postForEntity(uploadUrl, entity, String.class);

        return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + fullPath;
    }

    public void deleteFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            System.err.println("Delete skipped: Provided image URL is null or empty.");
            return;
        }

        try {
            String path = extractPathFromUrl(imageUrl);

            String deleteUrl = supabaseUrl + "/storage/v1/object/list/" + bucketName;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + serviceRoleKey);
            headers.set("apikey", serviceRoleKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Collections.singletonMap(
                    "prefixes",
                    Collections.singletonList(path)
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(deleteUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Successfully deleted file from Supabase: " + path);
            } else {
                System.err.println("Supabase responded with an error status: " + response.getStatusCode()
                        + " - " + response.getBody());
            }

        } catch (ApiException e) {
            System.err.println("Failed to parse image URL: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while deleting file: " + imageUrl);
            e.printStackTrace();
        }
    }
    private String extractPathFromUrl(String imageUrl) {
        String searchString = "/object/public/" + bucketName + "/";
        int startIndex = imageUrl.indexOf(searchString);
        if (startIndex != -1) {
            return imageUrl.substring(startIndex + searchString.length());
        }
        throw new ApiException(ErrorCode.FILE_003);
    }
}