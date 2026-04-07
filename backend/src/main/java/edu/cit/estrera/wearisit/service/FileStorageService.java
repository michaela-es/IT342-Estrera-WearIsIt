package edu.cit.estrera.wearisit.service;

import edu.cit.estrera.wearisit.api.ApiException;
import edu.cit.estrera.wearisit.api.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
}