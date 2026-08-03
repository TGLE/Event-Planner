package com.tgle.planner.core.errorhandling.exception;

import com.tgle.planner.token.domain.TokenType;
import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException {

    public enum Reason {
        EXPIRED,
        REVOKED,
        INVALID
    }

    private final Reason reason;
    private final TokenType tokenType;

    public InvalidTokenException(Reason reason, String message, TokenType tokenType) {
        super(message);
        this.reason = reason;
        this.tokenType = tokenType;
    }

    public InvalidTokenException(Reason reason, String message) {
        super(message);
        this.reason = reason;
        tokenType = null;
    }

    public static InvalidTokenException expired(TokenType tokenType) {
        return new InvalidTokenException(Reason.EXPIRED, "Token has already expired", tokenType);
    }

    public static InvalidTokenException revoked(TokenType tokenType) {
        return new InvalidTokenException(Reason.REVOKED, "Token has already been revoked", tokenType);
    }

    public static InvalidTokenException invalid(String message, TokenType tokenType) {
        return new InvalidTokenException(Reason.INVALID, message, tokenType);
    }

    public static InvalidTokenException invalid(String message) {
        return new InvalidTokenException(Reason.INVALID, message);
    }
}
