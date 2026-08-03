package com.tgle.planner.token.application.service;

import com.tgle.planner.token.domain.Token;
import com.tgle.planner.token.domain.TokenStatus;
import com.tgle.planner.token.domain.TokenType;
import com.tgle.planner.token.domain.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenStateService {

    private final Clock clock;
    private final TokenRepository tokenRepository;

    @Transactional
    public void activateToken(Token token) {
        if (token.isActive()) {
            return;
        }
        Token activatedToken = token.activate(Instant.now(clock));
        tokenRepository.save(activatedToken);
    }

    @Transactional
    public void revokeToken(Token token) {
        if (token.isRevoked()) {
            return;
        }
        Token revokedToken = token.revoke();
        tokenRepository.save(revokedToken);
    }

    @Transactional
    public Token useToken(Token token) {
        Token usedToken = token.use(Instant.now(clock));
        return tokenRepository.save(usedToken);
    }

    @Transactional
    public void revokeActiveTokensByTypeAndDeviceId(Long userId, TokenType type, String deviceId) {
        tokenRepository.updateStatusForTokensWithDeviceId(
                userId, type, deviceId, TokenStatus.ACTIVE, TokenStatus.REVOKED, Instant.now(clock)
        );
    }

    @Transactional
    public void revokeActiveTokensByType(Long userId, TokenType type) {
        tokenRepository.updateStatusForTokens(userId, type, TokenStatus.ACTIVE, TokenStatus.REVOKED, Instant.now(clock));
    }

    @Transactional
    public int deleteExpiredTokensByTypes(List<TokenType> tokenTypes, Instant threshold) {
        return tokenRepository.deleteExpiredTokensByTypes(tokenTypes, threshold);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeActiveTokensByTypeAndDeviceIdOnReuse(Long userId, TokenType type, String deviceId) {
        tokenRepository.updateStatusForTokensWithDeviceId(
                userId, type, deviceId, TokenStatus.ACTIVE, TokenStatus.REVOKED, Instant.now(clock)
        );
    }
}
