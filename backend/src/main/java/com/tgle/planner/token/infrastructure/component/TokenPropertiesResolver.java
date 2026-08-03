package com.tgle.planner.token.infrastructure.component;

import com.tgle.planner.core.properties.SecurityProperties;
import com.tgle.planner.token.domain.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TokenPropertiesResolver {

    private final SecurityProperties security;

    public Duration getExpiration(TokenType type, boolean rememberMe) {
        Duration expiration = switch (type) {
            case REFRESH -> rememberMe
                    ? security.token().refresh().rememberMeExpiration()
                    : security.token().refresh().expiration();
            case EMAIL_VERIFY -> security.token().otp().emailVerification().expiration();
            case EMAIL_CHANGE -> security.token().otp().emailChange().expiration();
            case PASSWORD_RESET -> security.token().otp().passwordReset().expiration();
            case PASSWORD_RESET_VERIFIED -> security.token().passwordResetVerified().expiration();
        };
        return Objects.requireNonNull(expiration, "Expiration configuration missing for: " + type);
    }

    public Duration getExpiration(TokenType type) {
        return getExpiration(type, false);
    }

    public String getCookieName(TokenType type) {
        String name = switch(type) {
            case REFRESH -> security.token().refresh().cookieName();
            case PASSWORD_RESET_VERIFIED -> security.token().passwordResetVerified().cookieName();
            default -> throw new IllegalArgumentException("Token type is not stored in a cookie: " + type);
        };
        return Objects.requireNonNull(name, "Cookie name configuration missing for: " + name);
    }
}
