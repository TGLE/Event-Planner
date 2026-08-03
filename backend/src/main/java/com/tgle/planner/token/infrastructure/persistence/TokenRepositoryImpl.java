package com.tgle.planner.token.infrastructure.persistence;

import com.tgle.planner.core.errorhandling.exception.ResourceNotFoundException;
import com.tgle.planner.token.domain.Token;
import com.tgle.planner.token.domain.TokenStatus;
import com.tgle.planner.token.domain.TokenType;
import com.tgle.planner.token.domain.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {

    private final TokenJpaRepository tokenJpaRepository;
    private final TokenPersistenceMapper tokenMapper;

    @Override
    public Optional<Token> findById(Long id) {
        return tokenJpaRepository.findById(id)
                .map(tokenMapper::toDomainEntity);
    }

    @Override
    public Optional<Token> findByTokenValueAndType(String tokenValue, TokenType type) {
        return tokenJpaRepository.findByTokenValueAndType(tokenValue, type)
                .map(tokenMapper::toDomainEntity);
    }

    @Override
    public Optional<Token> findByUserIdAndTokenValueAndType(Long userId, String tokenValue, TokenType type) {
        return tokenJpaRepository.findByUserIdAndTokenValueAndType(userId, tokenValue, type)
                .map(tokenMapper::toDomainEntity);
    }

    @Override
    public void updateStatusForTokens(
            Long userId,
            TokenType type,
            TokenStatus currentStatus,
            TokenStatus newStatus,
            Instant now
    ) {
        tokenJpaRepository.updateStatusForTokens(userId, type, currentStatus, newStatus, now);
    }

    @Override
    public void updateStatusForTokensWithDeviceId(
            Long userId,
            TokenType type,
            String deviceId,
            TokenStatus currentStatus,
            TokenStatus newStatus,
            Instant now
    ) {
        tokenJpaRepository.updateStatusForTokensWithDeviceId(userId, type, deviceId, currentStatus, newStatus, now);
    }

    @Override
    public int deleteExpiredTokensByTypes(List<TokenType> tokenTypes, Instant threshold) {
        return tokenJpaRepository.deleteExpiredTokensByTypes(tokenTypes, threshold);
    }

    @Override
    public Token save(Token token) {
        if (token.getId() == null) {
            TokenEntity entity = tokenMapper.toJpaEntity(token);
            return tokenMapper.toDomainEntity(tokenJpaRepository.save(entity));
        }
        TokenEntity existingEntity = tokenJpaRepository.findById(token.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Token"));
        tokenMapper.updateJpaEntity(token, existingEntity);
        return tokenMapper.toDomainEntity(existingEntity);
    }

    @Override
    public List<Token> saveAll(List<Token> tokens) {
        Set<Long> existingIds = new HashSet<>();

        for (Token token : tokens) {
            if (token.getId() == null) {
                continue;
            }

            if (!existingIds.add(token.getId())) {
                throw new IllegalArgumentException("Duplicate token id found: " + token.getId());
            }
        }

        if (existingIds.isEmpty()) {
            List<TokenEntity> entities = tokenMapper.toJpaEntityList(tokens);
            return tokenMapper.toDomainEntityList(tokenJpaRepository.saveAll(entities));
        }

        Map<Long, TokenEntity> existingEntities = tokenJpaRepository.findAllById(existingIds)
                .stream()
                .collect(Collectors.toMap(TokenEntity::getId, Function.identity()));

        if (existingEntities.size() < existingIds.size()) {
            Set<Long> missingIds = new HashSet<>(existingIds);
            missingIds.removeAll(existingEntities.keySet());
            throw new ResourceNotFoundException("Token", "ids", missingIds);
        }

        List<TokenEntity> entities = new ArrayList<>(tokens.size());
        for (Token token : tokens) {
            if (token.getId() == null) {
                entities.add(tokenMapper.toJpaEntity(token));
                continue;
            }
            TokenEntity existing = existingEntities.get(token.getId());
            tokenMapper.updateJpaEntity(token, existing);
            entities.add(existing);
        }
        return tokenMapper.toDomainEntityList(tokenJpaRepository.saveAll(entities));
    }
}
