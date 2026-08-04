package com.tgle.planner.token.domain;

import com.tgle.planner.core.errorhandling.exception.InvalidTokenException;
import com.tgle.planner.core.errorhandling.exception.TokenReuseException;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Token {
    @EqualsAndHashCode.Include
    private final Long id;
    private final String tokenValue;
    private final Instant expiresAt;
    @Builder.Default
    private final TokenStatus status = TokenStatus.ACTIVE;
    private final TokenType type;
    private final String deviceId;
    private final Long userId;
    @Singular("payload")
    private final Map<String, Object> payload;

    public static Token create(
            Long userId,
            TokenType type,
            String hashedValue,
            Instant expiration,
            String deviceId,
            Map<String, Object> payload
    ) {
        return Token.builder()
                .tokenValue(hashedValue)
                .expiresAt(expiration)
                .type(type)
                .userId(userId)
                .deviceId(deviceId)
                .payload(payload)
                .build();
    }

    public static Token create(
            Long userId,
            TokenStatus status,
            TokenType type,
            String hashedValue,
            Instant expiration,
            String deviceId,
            Map<String, Object> payload
    ) {
        return Token.builder()
                .tokenValue(hashedValue)
                .expiresAt(expiration)
                .status(status)
                .type(type)
                .userId(userId)
                .deviceId(deviceId)
                .payload(payload)
                .build();
    }

    public Token activate(Instant now) {
        if (isActive()) {
            return this;
        }

        if (isExpired(now)) {
            throw InvalidTokenException.expired(type);
        }

        if (isRevoked()) {
            throw InvalidTokenException.revoked(type);
        }

        if (isUsed()) {
            throw new TokenReuseException(type);
        }

        return toBuilder()
                .status(TokenStatus.ACTIVE)
                .build();
    }

    public Token revoke() {
        if (isRevoked()) {
            return this;
        }

        if (isUsed()) {
            throw new TokenReuseException(type);
        }

        return toBuilder()
                .status(TokenStatus.REVOKED)
                .build();
    }

    public Token use(Instant now) {
        if (isUsed()) {
            throw new TokenReuseException(type);
        }

        if (isExpired(now)) {
            throw InvalidTokenException.expired(type);
        }

        if (isRevoked()) {
            throw InvalidTokenException.revoked(type);
        }

        return toBuilder()
                .status(TokenStatus.USED)
                .build();
    }

    public boolean isActive() {
        return status == TokenStatus.ACTIVE;
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    public boolean isRevoked() {
        return status == TokenStatus.REVOKED;
    }

    public boolean isUsed() {
        return status == TokenStatus.USED;
    }

    public boolean isActiveAndExpired(Instant now) {
        return isActive() && isExpired(now);
    }

    public void ensureActive(Instant now) {
        switch (status) {
            case ACTIVE -> {
                if (isExpired(now)) {
                    throw InvalidTokenException.expired(type);
                }
            }
            case REVOKED -> throw InvalidTokenException.revoked(type);
            case USED -> throw new TokenReuseException(type);
        }
    }

    public void ensureDeviceMatches(String deviceId) {
        if (deviceId == null) {
            throw InvalidTokenException.invalid("Device ID is missing");
        }

        if (this.deviceId == null) {
            throw InvalidTokenException.invalid("Token is missing device ID", type);
        }

        if (!deviceId.equals(this.deviceId)) {
            throw InvalidTokenException.invalid("Token does not match with device id", type);
        }
    }

    public boolean isRememberMe() {
        return (boolean) payload.getOrDefault("rememberMe", false);
    }
}
