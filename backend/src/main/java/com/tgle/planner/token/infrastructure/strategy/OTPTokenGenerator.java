package com.tgle.planner.token.infrastructure.strategy;

import com.tgle.planner.core.properties.SecurityProperties;
import com.tgle.planner.token.application.TokenGenerationStrategy;
import com.tgle.planner.token.domain.TokenGenerationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class OTPTokenGenerator implements TokenGenerationStrategy {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final SecurityProperties security;

    @Override
    public TokenGenerationType getGenerationType() {
        return TokenGenerationType.OTP;
    }

    @Override
    public String generate() {
        int otpLength = security.token().otp().length();

        if (otpLength <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        StringBuilder code = new StringBuilder(otpLength);

        for (int i = 0; i < otpLength; i++) {
            int digit = SECURE_RANDOM.nextInt(10);
            code.append(digit);
        }
        return code.toString();
    }
}
