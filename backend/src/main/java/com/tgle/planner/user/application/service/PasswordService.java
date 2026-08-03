package com.tgle.planner.user.application.service;

import com.tgle.planner.core.errorhandling.exception.InvalidPasswordException;
import com.tgle.planner.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public void validateCurrentPassword(User user, String currentPassword) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidPasswordException("Current password does not match expected value");
        }
    }

    public void validateNewPassword(User user, String newPassword) {
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new InvalidPasswordException("New password must be different from current password");
        }
    }

    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
