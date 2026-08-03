package com.tgle.planner.token.application.service;

import com.tgle.planner.core.errorhandling.exception.ResourceNotFoundException;
import com.tgle.planner.token.domain.Token;
import com.tgle.planner.token.infrastructure.component.TokenHasher;
import com.tgle.planner.token.domain.TokenType;
import com.tgle.planner.token.domain.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenLookupService {

    private final TokenHasher tokenHasher;
    private final TokenRepository tokenRepository;

    public Token findByToken(String tokenValue, TokenType type) {
        String hashedToken = tokenHasher.hashToken(tokenValue);
        return tokenRepository.findByTokenValueAndType(hashedToken, type)
                .orElseThrow(() -> new ResourceNotFoundException(type.getDescription()));
    }

    public Token findByToken(Long userId, String tokenValue, TokenType type) {
        String hashedToken = tokenHasher.hashToken(tokenValue);
        return tokenRepository.findByUserIdAndTokenValueAndType(userId, hashedToken, type)
                .orElseThrow(() -> new ResourceNotFoundException(type.getDescription()));
    }

    public Token findById(Long id) {
        return tokenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Token"));
    }
}
