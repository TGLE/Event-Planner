package com.tgle.planner.token.infrastructure.persistence;

import com.tgle.planner.token.domain.TokenStatus;
import com.tgle.planner.token.domain.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TokenJpaRepository extends JpaRepository<TokenEntity, Long> {
    Optional<TokenEntity> findByTokenValueAndType(String tokenValue, TokenType type);

    Optional<TokenEntity> findByUserIdAndTokenValueAndType(Long userId, String tokenValue, TokenType type);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE TokenEntity t
            SET t.status = :newStatus, t.updatedAt = :now
            WHERE t.user.id = :userId AND t.type = :type AND t.status = :currentStatus
            """)
    void updateStatusForTokens(
            @Param("userId") Long userId,
            @Param("type") TokenType type,
            @Param("currentStatus") TokenStatus currentStatus,
            @Param("newStatus") TokenStatus newStatus,
            @Param("now") Instant now
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE TokenEntity t
            SET t.status = :newStatus, t.updatedAt = :now
            WHERE t.user.id = :userId AND t.type = :type AND t.deviceId = :deviceId AND t.status = :currentStatus
            """)
    void updateStatusForTokensWithDeviceId(
            @Param("userId") Long userId,
            @Param("type") TokenType type,
            @Param("deviceId") String deviceId,
            @Param("currentStatus") TokenStatus currentStatus,
            @Param("newStatus") TokenStatus newStatus,
            @Param("now") Instant now
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            DELETE FROM TokenEntity t
            WHERE t.type IN :types
            AND t.expiresAt < :threshold
            """)
    int deleteExpiredTokensByTypes(List<TokenType> types, Instant threshold);
}
