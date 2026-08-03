package com.tgle.planner.core.errorhandling.exception;

import lombok.Getter;

@Getter
public class TokenCooldownException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Token cooldown is active. Please wait before requesting new code";

    public TokenCooldownException(String message) {
        super(message);
    }

    public TokenCooldownException() {
        super(DEFAULT_MESSAGE);
    }
}
