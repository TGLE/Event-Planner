package com.tgle.planner.user.domain;

import com.tgle.planner.core.errorhandling.exception.InvalidEmailException;
import com.tgle.planner.core.errorhandling.exception.TokenCooldownException;
import com.tgle.planner.core.errorhandling.exception.UserAlreadyVerifiedException;
import com.tgle.planner.core.errorhandling.exception.UserNotVerifiedException;
import com.tgle.planner.token.domain.TokenType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @EqualsAndHashCode.Include
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    @Singular
    private final List<Role> roles;
    private final boolean locked;
    private final boolean enabled;
    @Singular
    private final Map<TokenType, TokenCooldown> cooldowns;

    public static User create(
            String firstName,
            String lastName,
            String email,
            String password,
            List<Role> roles,
            boolean enabled
    ) {
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(password)
                .roles(roles)
                .enabled(enabled)
                .build();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public User updateEmail(String email) {
        return toBuilder()
                .email(email)
                .build();
    }

    public User updatePassword(String hashedPassword) {
        return toBuilder()
                .password(hashedPassword)
                .build();
    }

    public User lock() {
        if (locked) {
            return this;
        }
        return toBuilder()
                .locked(true)
                .build();
    }

    public User unlock() {
        if (!locked) {
            return this;
        }
        return toBuilder()
                .locked(false)
                .build();
    }

    public User enable() {
        if (enabled) {
            return this;
        }
        return toBuilder()
                .enabled(true)
                .build();
    }

    public User disable() {
        if (!enabled) {
            return this;
        }
        return toBuilder()
                .enabled(false)
                .build();
    }

    public void ensureUniqueEmail(String newEmail) {
        if (email.equalsIgnoreCase(newEmail)) {
            throw new InvalidEmailException("New email must be different from current email");
        }
    }

    public void ensureNotEnabled() {
        if (isEnabled()) {
            throw new UserAlreadyVerifiedException("User account is already verified");
        }
    }

    public void ensureEnabled() {
        if (!isEnabled()) {
            throw new UserNotVerifiedException("User account has not been verified");
        }
    }

    public void checkCooldown(TokenType type, Duration resetWindow, List<Duration> cooldowns, Instant now) {
        TokenCooldown cooldown = this.cooldowns.get(type);
        if (cooldown == null || cooldown.getResendCount() == 0) {
            return;
        }
        if (cooldown.isCycleExpired(resetWindow, now)) {
            return;
        }
        if (cooldown.isCooldownActive(cooldowns, now)) {
            throw new TokenCooldownException();
        }
    }

    public User advanceCooldown(TokenType type, Duration resetWindow, Instant now) {
        TokenCooldown current = cooldowns.getOrDefault(type, TokenCooldown.builder()
                .resendCount(0)
                .build());

        TokenCooldown updated = current.next(resetWindow, now);
        return this.toBuilder()
                .cooldown(type, updated)
                .build();
    }
}
