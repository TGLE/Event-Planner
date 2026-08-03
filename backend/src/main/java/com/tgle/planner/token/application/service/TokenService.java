package com.tgle.planner.token.application.service;

import com.tgle.planner.core.errorhandling.exception.TokenReuseException;
import com.tgle.planner.token.domain.Token;
import com.tgle.planner.token.domain.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final Clock clock;
    private final TokenCreationService tokenCreationService;
    private final TokenStateService tokenStateService;
    private final TokenLookupService tokenLookupService;

    public String issueVerificationToken(Long userId) {
        TokenType type = TokenType.EMAIL_VERIFY;
        tokenStateService.revokeActiveTokensByType(userId, type);
        return tokenCreationService.createToken(userId, type);
    }

    public String issueResendVerificationToken(Long userId) {
        TokenType type = TokenType.EMAIL_VERIFY;
        tokenStateService.revokeActiveTokensByType(userId, type);
        return tokenCreationService.createToken(userId, type);
    }

    public String issueRefreshToken(Long userId, String deviceId, boolean rememberMe) {
        tokenStateService.revokeActiveTokensByTypeAndDeviceId(userId, TokenType.REFRESH, deviceId);
        return tokenCreationService.createRefreshToken(
                userId, deviceId, rememberMe, Map.of("rememberMe", rememberMe)
        );
    }

    public String rotateRefreshToken(Token token, String deviceId) {
        tokenStateService.useToken(token);
        boolean rememberMe = token.isRememberMe();
        return tokenCreationService.createRefreshToken(
                token.getUserId(), deviceId, rememberMe, Map.of("rememberMe", rememberMe)
        );
    }

    public String issuePasswordResetToken(Long userId) {
        TokenType type = TokenType.PASSWORD_RESET;
        tokenStateService.revokeActiveTokensByType(userId, type);
        return tokenCreationService.createToken(userId, type);
    }

    public String issuePasswordResetVerifiedToken(Long userId) {
        TokenType type = TokenType.PASSWORD_RESET_VERIFIED;
        tokenStateService.revokeActiveTokensByType(userId, type);
        return tokenCreationService.createToken(userId, type);
    }

    public String issueEmailChangeToken(Long userId, String pendingEmail) {
        TokenType type = TokenType.EMAIL_CHANGE;
        tokenStateService.revokeActiveTokensByType(userId, type);
        return tokenCreationService.createToken(userId, type, Map.of("pendingEmail", pendingEmail));
    }

    @Transactional
    public String createOneTimeToken(Long userId, TokenType type) {
        tokenStateService.revokeActiveTokensByType(userId, type);
        return tokenCreationService.createToken(userId, type);
    }

    @Transactional
    public void revokeAllRefreshTokens(Long userId) {
        tokenStateService.revokeActiveTokensByType(userId, TokenType.REFRESH);
    }

    @Transactional
    public Token validateRefreshTokenForRotation(String tokenValue, String deviceId) {
        Token token = tokenLookupService.findByToken(tokenValue, TokenType.REFRESH);
        try {
            validateRefreshToken(token, deviceId);
        } catch (TokenReuseException e) {
            tokenStateService.revokeActiveTokensByTypeAndDeviceId(token.getUserId(), TokenType.REFRESH, deviceId);
            throw e;
        }
        return token;
    }

    public void validateAndRevokeRefreshToken(String tokenValue, String deviceId) {
        Token token = tokenLookupService.findByToken(tokenValue, TokenType.REFRESH);
        validateRefreshToken(token, deviceId);
        tokenStateService.revokeToken(token);
    }

    public Token validatePasswordResetVerifiedToken(String tokenValue) {
        Token token = tokenLookupService.findByToken(tokenValue, TokenType.PASSWORD_RESET_VERIFIED);
        token.ensureActive(Instant.now(clock));
        return token;
    }

    public Token verifyAndUseToken(Long userId, String tokenValue, TokenType type) {
        Token token = tokenLookupService.findByToken(userId, tokenValue, type);
        return tokenStateService.useToken(token);
    }

    private void validateRefreshToken(Token token, String deviceId) {
        token.ensureActive(Instant.now(clock));
        token.ensureDeviceMatches(deviceId);
    }
}
