package com.tgle.planner.token.application;

import com.tgle.planner.token.infrastructure.component.TokenHasher;
import com.tgle.planner.token.domain.Token;
import com.tgle.planner.token.domain.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TokenGenerator {

    private final TokenHasher tokenHasher;
    private final TokenGenerationFactory tokenGenerationFactory;

    public GeneratedToken generateToken(
            Long userId, TokenType type, Instant expiration, String deviceId, Map<String, Object> payload
    ) {
        String rawValue = tokenGenerationFactory.generateToken(type);
        String hashedValue = tokenHasher.hashToken(rawValue);
        Token token = Token.create(userId, type, hashedValue, expiration, deviceId, payload);
        return new GeneratedToken(token, rawValue);
    }
}
