package com.tgle.planner.core.errorhandling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_001", "An unexpected error occurred"),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_002", "Failed to send email"),

    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_001", "Request validation failed"),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "VALIDATION_002", "Request arguments are invalid"),

    DATA_CONFLICT(HttpStatus.CONFLICT, "DATA_001", "Request conflicts with existing data"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "DATA_002", "Resource not found"),
    RESOURCE_ALREADY_EXISTS(HttpStatus.CONFLICT, "DATA_003", "Resource already exists"),

    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_001", "Invalid email or password"),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_002", "Authentication failed"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_003", "You don't have permission to access this resource"),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "AUTH_004", "User account is disabled (or not verified!)"),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "AUTH_005", "User account is locked"),
    ACCOUNT_NOT_VERIFIED(HttpStatus.FORBIDDEN, "AUTH_006", "Account is not verified"),
    ACCOUNT_VERIFIED(HttpStatus.CONFLICT, "AUTH_007", "Account is verified"),
    SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_008", "Session has expired"),

    INVALID_JWT(HttpStatus.UNAUTHORIZED, "TOKEN_001", "JWT token is invalid or malformed"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_002", "Token is invalid or malformed"),
    TOKEN_COOLDOWN(HttpStatus.TOO_MANY_REQUESTS, "TOKEN_003", "Please wait before requesting a new token");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
