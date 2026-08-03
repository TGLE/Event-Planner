package com.tgle.planner.profile.application;

import com.tgle.planner.profile.application.dto.ChangeEmailCommand;
import com.tgle.planner.profile.application.dto.ChangePasswordCommand;
import com.tgle.planner.profile.application.dto.VerifyEmailChangeCommand;
import com.tgle.planner.profile.application.event.PasswordChangeConfirmedEvent;
import com.tgle.planner.profile.application.event.EmailChangeRequestedEvent;
import com.tgle.planner.profile.application.event.EmailChangeConfirmedEvent;
import com.tgle.planner.token.domain.Token;
import com.tgle.planner.token.domain.TokenType;
import com.tgle.planner.token.application.service.TokenService;
import com.tgle.planner.user.domain.User;
import com.tgle.planner.user.application.service.PasswordService;
import com.tgle.planner.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final PasswordService passwordService;
    private final UserService userService;
    private final TokenService tokenService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void changePassword(ChangePasswordCommand command) {
        User user = userService.findEnabledUser(command.userId());
        passwordService.validateCurrentPassword(user, command.currentPassword());
        String newPassword = command.newPassword();
        passwordService.validateNewPassword(user, newPassword);
        User updatedUser = userService.updateUserPassword(user, newPassword);
        tokenService.revokeAllRefreshTokens(updatedUser.getId());
        applicationEventPublisher.publishEvent(
                new PasswordChangeConfirmedEvent(updatedUser.getFullName(), updatedUser.getEmail())
        );
    }

    @Transactional
    public void changeEmail(ChangeEmailCommand command) {
        User user = userService.findEnabledUser(command.userId());
        passwordService.validateCurrentPassword(user, command.currentPassword());
        String newEmail = command.newEmail();
        userService.ensureUniqueEmailAndNotTaken(user, newEmail);
        User updatedUser = userService.validateAndAdvanceCooldown(user, TokenType.EMAIL_CHANGE);
        String token = tokenService.issueEmailChangeToken(updatedUser.getId(), newEmail);
        applicationEventPublisher.publishEvent(
                new EmailChangeRequestedEvent(updatedUser.getFullName(), newEmail, token)
        );
    }

    @Transactional
    public void verifyEmailChange(VerifyEmailChangeCommand command) {
        Long userId = command.userId();
        User user = userService.findEnabledUser(userId);
        String oldEmail = user.getEmail();
        Token token = tokenService.verifyAndUseToken(userId, command.tokenValue(), TokenType.EMAIL_CHANGE);
        String newEmail = (String) token.getPayload().get("pendingEmail");
        User updatedUser = userService.updateUserEmail(user, newEmail);
        tokenService.revokeAllRefreshTokens(updatedUser.getId());
        applicationEventPublisher.publishEvent(
                new EmailChangeConfirmedEvent(updatedUser.getFullName(), oldEmail, newEmail)
        );
    }

    @Transactional
    public void logoutAll(Long userId) {
        tokenService.revokeAllRefreshTokens(userId);
    }
}
