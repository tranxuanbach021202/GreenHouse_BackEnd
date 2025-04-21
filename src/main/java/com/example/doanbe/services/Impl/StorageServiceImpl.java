package com.example.doanbe.services.Impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.doanbe.config.R2Config;
import com.example.doanbe.contanst.StorageConstant;
import com.example.doanbe.dto.request.PresignedUrlRequest;
import com.example.doanbe.dto.response.PresignedUrlResponse;
import com.example.doanbe.exception.StorageException;
import com.example.doanbe.services.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final AmazonS3 r2Client;
    private final R2Config r2Config;

    @Override
    public PresignedUrlResponse generateUploadUrl(PresignedUrlRequest request) {
        try {
            String objectKey = generateObjectKey(request.getFileName(), request.getFolder());
            long expirationTime = request.getExpirationTime() != null ?
                    request.getExpirationTime() : StorageConstant.DEFAULT_EXPIRATION_TIME;

            GeneratePresignedUrlRequest presignedUrlRequest = new GeneratePresignedUrlRequest(
                    r2Config.getBucketName(),
                    objectKey)
                    .withMethod(HttpMethod.PUT)
                    .withExpiration(getExpirationTime(expirationTime))
                    .withContentType(request.getContentType());

            URL presignedUrl = r2Client.generatePresignedUrl(presignedUrlRequest);

            return PresignedUrlResponse.builder()
                    .presignedUrl(presignedUrl.toString())
                    .objectKey(objectKey)
                    .expirationTime(expirationTime)
                    .build();
        } catch (Exception e) {
            log.error("Error generating upload URL: ", e);
            throw new StorageException("Failed to generate upload URL", "UPLOAD_URL_ERROR");
        }
    }

    @Override
    public PresignedUrlResponse generateDownloadUrl(String objectKey, Long expirationTime) {
        try {
            if (!doesObjectExist(objectKey)) {
                throw new StorageException("File not found", "FILE_NOT_FOUND");
            }

            long expTime = expirationTime != null ?
                    expirationTime : StorageConstant.DEFAULT_EXPIRATION_TIME;

            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                    r2Config.getBucketName(),
                    objectKey)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(getExpirationTime(expTime));

            URL presignedUrl = r2Client.generatePresignedUrl(request);

            return PresignedUrlResponse.builder()
                    .presignedUrl(presignedUrl.toString())
                    .objectKey(objectKey)
                    .expirationTime(expTime)
                    .build();
        } catch (Exception e) {
            log.error("Error generating download URL: ", e);
            throw new StorageException("Failed to generate download URL", "DOWNLOAD_URL_ERROR");
        }
    }

    @Override
    public boolean doesObjectExist(String objectKey) {
        try {
            return r2Client.doesObjectExist(r2Config.getBucketName(), objectKey);
        } catch (Exception e) {
            log.error("Error checking object existence: ", e);
            return false;
        }
    }

    private String generateObjectKey(String fileName, String folder) {
        return folder + "/" +
                UUID.randomUUID().toString();
    }

    private Date getExpirationTime(long expirationTimeInSeconds) {
        return new Date(System.currentTimeMillis() + (expirationTimeInSeconds * 1000));
    }
}