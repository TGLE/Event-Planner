package com.tgle.planner.user.application.service;

import com.tgle.planner.core.properties.SecurityProperties;
import com.tgle.planner.token.domain.TokenType;
import com.tgle.planner.user.domain.User;
import com.tgle.planner.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CooldownService {

    private final Clock clock;
    private final SecurityProperties security;
    private final UserRepository userRepository;

    public void checkTokenCooldown(User user, TokenType type) {
        Instant now = Instant.now(clock);
        user.checkCooldown(
                type,
                security.token().otp().cooldownResetPeriod(),
                security.token().otp().cooldowns(),
                now);
    }

    public User advanceTokenCooldown(User user, TokenType type) {
        Instant now = Instant.now(clock);
        User updatedUser = user.advanceCooldown(type, security.token().otp().cooldownResetPeriod(), now);
        return userRepository.save(updatedUser);
    }
}
