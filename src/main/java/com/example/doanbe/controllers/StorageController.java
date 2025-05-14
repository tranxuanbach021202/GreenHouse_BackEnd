package com.example.doanbe.controllers;

import com.example.doanbe.dto.request.PresignedUrlRequest;
import com.example.doanbe.dto.response.PresignedUrlResponse;
import com.example.doanbe.payload.response.SuccessResponse;
import com.example.doanbe.services.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/storage")
@Validated
@RequiredArgsConstructor
@Slf4j
public class StorageController {

    private final StorageService storageService;

    @PostMapping("/upload-url")
    public ResponseEntity<SuccessResponse> getUploadUrl(
            @Valid @RequestBody PresignedUrlRequest request) {
        log.info("Generating upload URL for file: {}", request.getFileName());
        PresignedUrlResponse response = storageService.generateUploadUrl(request);
        return ResponseEntity.ok(new SuccessResponse(response));
    }

    @GetMapping("/image-url/{objectKey}")
    public ResponseEntity<SuccessResponse> getDownloadUrl(
            @PathVariable String objectKey,
            @RequestParam(required = false) Long expirationTime) {
        log.info("Generating download URL for object: {}", objectKey);
        PresignedUrlResponse response = storageService.generateDownloadUrl(objectKey, expirationTime);
        return ResponseEntity.ok(new SuccessResponse(response));
    }
}
