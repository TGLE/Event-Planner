package com.tgle.planner.token.application.service;

import com.tgle.planner.token.application.GeneratedToken;
import com.tgle.planner.token.application.TokenGenerator;
import com.tgle.planner.token.infrastructure.component.TokenPropertiesResolver;
import com.tgle.planner.token.domain.TokenType;
import com.tgle.planner.token.domain.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenCreationService {

    private final Clock clock;
    private final TokenPropertiesResolver tokenProperties;
    private final TokenRepository tokenRepository;
    private final TokenGenerator tokengenerator;

    public String createRefreshToken(Long userId, String deviceId, boolean rememberMe, Map<String, Object> payload) {
        Instant now = Instant.now(clock);
        Instant expiration = now.plus(tokenProperties.getExpiration(TokenType.REFRESH, rememberMe));
        return generateAndSaveToken(userId, TokenType.REFRESH, expiration, deviceId, payload);
    }

    public String createToken(Long userId, TokenType type) {
        return createToken(userId, type, null, Map.of());
    }

    public String createToken(Long userId, TokenType type, String deviceId) {
        return createToken(userId, type, deviceId, Map.of());
    }

    public String createToken(Long userId, TokenType type, Map<String, Object> payload) {
        return createToken(userId, type, null, payload);
    }

    public String createToken(Long userId, TokenType type, String deviceId, Map<String, Object> payload) {
        Instant now = Instant.now(clock);
        Instant expiration = now.plus(tokenProperties.getExpiration(type));
        return generateAndSaveToken(userId, type, expiration, deviceId, payload);
    }

    private String generateAndSaveToken(
            Long userId, TokenType type, Instant expiration, String deviceId, Map<String, Object> payload
    ) {
        GeneratedToken generatedToken = tokengenerator.generateToken(userId, type, expiration, deviceId, payload);
        tokenRepository.save(generatedToken.token());
        return generatedToken.rawValue();
    }
}
