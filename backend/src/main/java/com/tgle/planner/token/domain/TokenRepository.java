package com.tgle.planner.token.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TokenRepository {
    Optional<Token> findById(Long id);

    Optional<Token> findByTokenValueAndType(String tokenValue, TokenType type);

    Optional<Token> findByUserIdAndTokenValueAndType(Long userId, String tokenValue, TokenType type);

    void updateStatusForTokens(
            Long userId, TokenType type, TokenStatus currentStatus, TokenStatus newStatus, Instant now
    );

    void updateStatusForTokensWithDeviceId(
            Long userId, TokenType type, String deviceId, TokenStatus currentStatus, TokenStatus newStatus, Instant now
    );

    int deleteExpiredTokensByTypes(List<TokenType> tokenTypes, Instant threshold);

    Token save(Token token);

    List<Token> saveAll(List<Token> tokens);
}
