package com.tgle.planner.core.errorhandling;

import com.tgle.planner.core.errorhandling.exception.*;
import com.tgle.planner.core.security.CookieService;
import com.tgle.planner.token.domain.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Clock clock;
    private final CookieService cookieService;

    @ExceptionHandler(InvalidBearerTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBearerTokenException(InvalidBearerTokenException ex) {
        return buildErrorResponse(ErrorCode.INVALID_JWT, ex);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        return buildErrorResponse(ErrorCode.AUTHENTICATION_FAILED, ex);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> handleInternalAuthenticationServiceException(
            InternalAuthenticationServiceException ex) {
        return buildErrorResponse(ErrorCode.INVALID_CREDENTIALS, ex);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return buildErrorResponse(ErrorCode.INVALID_CREDENTIALS, ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return buildErrorResponse(ErrorCode.ACCESS_DENIED, ex);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLockedException(LockedException ex) {
        return buildErrorResponse(ErrorCode.ACCOUNT_LOCKED, ex);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabledException(DisabledException ex) {
        return buildErrorResponse(ErrorCode.ACCOUNT_DISABLED, ex);
    }

    @ExceptionHandler(TokenCooldownException.class)
    public ResponseEntity<ErrorResponse> handleTokenCooldownException(TokenCooldownException ex) {
        return buildErrorResponse(ErrorCode.TOKEN_COOLDOWN, ex);
    }

    @ExceptionHandler(TokenReuseException.class)
    public ResponseEntity<ErrorResponse> handleTokenReuseException(
            TokenReuseException ex, HttpServletRequest request
    ) {
        TokenType tokenType = ex.getTokenType();
        ErrorCode errorCode = ErrorCode.INVALID_TOKEN;
        ResponseEntity<ErrorResponse> response = buildErrorResponse(errorCode, ex);

        if (tokenType != null && tokenType.isStoredInCookie() && cookieService.hasCookie(request, tokenType)) {
            ResponseCookie cookie = cookieService.removeTokenCookie(tokenType);
            return ResponseEntity.status(errorCode.getStatus())
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response.getBody());
        }
        return response;
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(
            InvalidTokenException ex, HttpServletRequest request
    ) {
        TokenType tokenType = ex.getTokenType();
        boolean isCookieToken = tokenType != null && tokenType.isStoredInCookie();

        ErrorCode errorCode = switch (ex.getReason()) {
            case EXPIRED, REVOKED -> isCookieToken ? ErrorCode.SESSION_EXPIRED : ErrorCode.INVALID_TOKEN;
            case INVALID -> ErrorCode.INVALID_TOKEN;
        };

        if (isCookieToken && cookieService.hasCookie(request, tokenType)) {
            ResponseCookie cookie = cookieService.removeTokenCookie(tokenType);
            ResponseEntity<ErrorResponse> response = buildErrorResponse(errorCode, ex);
            return ResponseEntity.status(errorCode.getStatus())
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response.getBody());
        }
        return buildErrorResponse(errorCode, ex);
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleUserNotVerifiedException(UserNotVerifiedException ex) {
        return buildErrorResponse(ErrorCode.ACCOUNT_NOT_VERIFIED, ex);
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyVerifiedException(UserAlreadyVerifiedException ex) {
        return buildErrorResponse(ErrorCode.ACCOUNT_VERIFIED, ex);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        return buildErrorResponse(ErrorCode.RESOURCE_ALREADY_EXISTS, ex);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return buildErrorResponse(ErrorCode.RESOURCE_NOT_FOUND, ex);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return buildErrorResponse(ErrorCode.DATA_CONFLICT, ex);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(ErrorCode.INVALID_ARGUMENT, ex);
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<ErrorResponse> handleEmailSendException(EmailSendException ex) {
        return buildErrorResponse(ErrorCode.EMAIL_SEND_FAILED, ex);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordException(InvalidPasswordException ex) {
        return buildErrorResponse(ErrorCode.INVALID_ARGUMENT, ex);
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEmailException(InvalidEmailException ex) {
        return buildErrorResponse(ErrorCode.INVALID_ARGUMENT, ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage
                ));

        return buildErrorResponse(ErrorCode.VALIDATION_FAILED, errors, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = ex
                .getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        return buildErrorResponse(ErrorCode.VALIDATION_FAILED, errors, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, ex);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode, Exception exception) {
        return buildErrorResponse(errorCode, null, exception);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            ErrorCode errorCode,
            Map<String, String> validationErrors,
            Exception exception) {

        log.error("Exception caught: {}, {}", errorCode.name(), errorCode.getCode(), exception);

        HttpStatus status = errorCode.getStatus();

        ErrorResponse response = ErrorResponse.builder()
                .errorCode(errorCode.getCode())
                .statusCode(status.value())
                .statusName(status.name())
                .errorMessage(errorCode.getMessage())
                .validationErrors(validationErrors)
                .details(exception.getMessage())
                .timestamp(Instant.now(clock))
                .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }
}
