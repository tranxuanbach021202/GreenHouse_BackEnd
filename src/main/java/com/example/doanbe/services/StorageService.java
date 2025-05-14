package com.example.doanbe.services;

import com.example.doanbe.dto.request.PresignedUrlRequest;
import com.example.doanbe.dto.response.PresignedUrlResponse;

public interface StorageService {

    PresignedUrlResponse generateUploadUrl(PresignedUrlRequest request);

    PresignedUrlResponse generateDownloadUrl(String objectKey, Long expirationTime);
    boolean doesObjectExist(String objectKey);
}
