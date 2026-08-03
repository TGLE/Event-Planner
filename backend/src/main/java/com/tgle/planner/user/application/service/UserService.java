package com.tgle.planner.user.application.service;

import com.tgle.planner.token.domain.TokenType;
import com.tgle.planner.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CooldownService cooldownService;
    private final UserCreationService userCreationService;
    private final UserLookupService userLookupService;
    private final UserStateService userStateService;

    public User registerUser(String firstName, String lastName, String email, String password) {
        userLookupService.ensureEmailNotTaken(email);
        return userCreationService.createDefaultUser(firstName, lastName, email, password);
    }

    public User registerAdmin(String firstName, String lastName, String email, String password) {
        userLookupService.ensureEmailNotTaken(email);
        return userCreationService.createAdminUser(firstName, lastName, email, password);
    }

    public User validateAndAdvanceCooldown(User user, TokenType type) {
        cooldownService.checkTokenCooldown(user, type);
        return cooldownService.advanceTokenCooldown(user, type);
    }

    public User findEnabledUser(Long id) {
        User user = userLookupService.findById(id);
        user.ensureEnabled();
        return user;
    }

    public User findEnabledUser(String email) {
        User user = userLookupService.findByEmail(email);
        user.ensureEnabled();
        return user;
    }

    public User findUnverifiedUser(String email) {
        User user = userLookupService.findByEmail(email);
        user.ensureNotEnabled();
        return user;
    }

    public void ensureUniqueEmailAndNotTaken(User user, String email) {
        user.ensureUniqueEmail(email);
        userLookupService.ensureEmailNotTaken(email);
    }

    public User findUserById(Long id) {
        return userLookupService.findById(id);
    }

    public User updateUserPassword(User user, String newPassword) {
        return userStateService.updateUserPassword(user, newPassword);
    }

    public User updateUserEmail(User user, String newEmail) {
        return userStateService.updateUserEmail(user, newEmail);
    }

    public void enableUser(User user) {
        userStateService.enableUser(user);
    }
}
