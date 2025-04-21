package com.example.doanbe.services.Impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;

@Service
@Slf4j
public class R2Service {

    @Autowired
    private AmazonS3 r2Client;

    @Value("${bucket-name}")
    private String bucketName;

    /**
     * Tạo Presigned URL để upload file
     */
    public String generatePresignedUrlForUpload(String objectKey, String contentType, long expirationTimeInSeconds) {
        try {
            // Tạo metadata cho file
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);

            // Tạo request để generate URL
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectKey)
                    .withMethod(HttpMethod.PUT)
                    .withExpiration(getExpirationTime(expirationTimeInSeconds))
                    .withContentType(contentType);

            // Generate URL
            URL presignedUrl = r2Client.generatePresignedUrl(request);
            return presignedUrl.toString();
        } catch (Exception e) {
            log.error("Error generating presigned URL for upload: ", e);
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    /**
     * Tạo Presigned URL để download file
     */
    public String generatePresignedUrlForDownload(String objectKey, long expirationTimeInSeconds) {
        try {
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectKey)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(getExpirationTime(expirationTimeInSeconds));

            URL presignedUrl = r2Client.generatePresignedUrl(request);
            return presignedUrl.toString();
        } catch (Exception e) {
            log.error("Error generating presigned URL for download: ", e);
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    private Date getExpirationTime(long expirationTimeInSeconds) {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime() + (expirationTimeInSeconds * 1000);
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    /**
     * Kiểm tra file có tồn tại trong bucket không
     */
    public boolean doesObjectExist(String objectKey) {
        try {
            return r2Client.doesObjectExist(bucketName, objectKey);
        } catch (Exception e) {
            log.error("Error checking object existence: ", e);
            return false;
        }
    }
}

