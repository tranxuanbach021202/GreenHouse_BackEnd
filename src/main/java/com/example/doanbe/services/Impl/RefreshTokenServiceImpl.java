package com.example.doanbe.services.Impl;

import com.example.doanbe.document.RefreshToken;
import com.example.doanbe.exception.TokenRefreshException;
import com.example.doanbe.repository.RefreshTokenRepository;
import com.example.doanbe.repository.UserRepository;
import com.example.doanbe.services.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpirytion(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Override
    public RefreshToken createRefreshToken(String userId) {
        RefreshToken refreshToken = new RefreshToken();

        // Xóa token cũ
        refreshTokenRepository.deleteByUserId(userId);

        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Transactional
    @Override
    public int deleteByUserId(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
        return 0;
    }
}
