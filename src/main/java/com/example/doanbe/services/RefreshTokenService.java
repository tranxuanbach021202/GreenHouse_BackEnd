package com.example.doanbe.services;

import com.example.doanbe.document.RefreshToken;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);
    RefreshToken verifyExpirytion(RefreshToken token);
    RefreshToken createRefreshToken(String userId);

    @Transactional
    int deleteByUserId(String userId);
}
