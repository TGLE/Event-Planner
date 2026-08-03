package com.tgle.planner.core.errorhandling.exception;

import com.tgle.planner.token.domain.TokenType;
import lombok.Getter;

@Getter
public class TokenReuseException extends RuntimeException {

    private final TokenType tokenType;

    private static final String DEFAULT_MESSAGE =
            "Token reuse detected. The token and its associated sessions have been invalidated for security reasons";

    public TokenReuseException(String message, TokenType tokenType) {
        super(message);
        this.tokenType = tokenType;
    }

    public TokenReuseException(String message) {
        super(message);
        tokenType = null;
    }

    public TokenReuseException(TokenType tokenType) {
        super(DEFAULT_MESSAGE);
        this.tokenType = tokenType;
    }

    public TokenReuseException() {
        super(DEFAULT_MESSAGE);
        tokenType = null;
    }
}
