package com.example.doanbe.repository;

import com.example.doanbe.document.RefreshToken;
import com.example.doanbe.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    void deleteByUserId(String userId);
}
