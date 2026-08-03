package com.tgle.planner.token.infrastructure.strategy;

import com.tgle.planner.token.application.TokenGenerationStrategy;
import com.tgle.planner.token.domain.TokenGenerationType;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class OpaqueTokenGenerator implements TokenGenerationStrategy {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public TokenGenerationType getGenerationType() {
        return TokenGenerationType.OPAQUE;
    }

    @Override
    public String generate() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
