package com.example.doanbe.repository;

import com.example.doanbe.document.DeviceToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DeviceTokenRepository extends MongoRepository<DeviceToken, String> {
    Optional<DeviceToken> findByUserIdAndToken(String userId, String token);
    void deleteByUserIdAndToken(String userId, String token);
}
