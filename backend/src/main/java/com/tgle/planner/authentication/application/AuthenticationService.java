package com.tgle.planner.authentication.application;

import com.tgle.planner.authentication.application.dto.*;
import com.tgle.planner.authentication.application.event.PasswordResetRequestedEvent;
import com.tgle.planner.authentication.application.event.VerifyEmailRequestedEvent;
import com.tgle.planner.authentication.application.event.PasswordResetConfirmedEvent;
import com.tgle.planner.core.errorhandling.exception.ResourceNotFoundException;
import com.tgle.planner.core.security.JwtService;
import com.tgle.planner.core.errorhandling.exception.InvalidTokenException;
import com.tgle.planner.core.errorhandling.exception.TokenCooldownException;
import com.tgle.planner.core.errorhandling.exception.TokenReuseException;
import com.tgle.planner.core.errorhandling.exception.UserNotVerifiedException;
import com.tgle.planner.token.domain.Token;
import com.tgle.planner.token.domain.TokenType;
import com.tgle.planner.user.domain.User;
import com.tgle.planner.token.application.service.TokenService;
import com.tgle.planner.user.infrastructure.security.UserPrincipal;
import com.tgle.planner.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final TokenService tokenService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void register(RegisterCommand command) {
        User user = userService.registerUser(
                command.firstName(),
                command.lastName(),
                command.email(),
                command.password()
        );
        String token = tokenService.issueVerificationToken(user.getId());
        applicationEventPublisher.publishEvent(
                new VerifyEmailRequestedEvent(user.getFullName(), user.getEmail(), token)
        );
    }

    @Transactional
    public AuthenticationResult login(LoginCommand command) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(command.email(), command.password())
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        boolean rememberMe = command.rememberMe();

        var accessToken = jwtService.createAccessToken(authentication);
        var refreshToken = tokenService.issueRefreshToken(userPrincipal.getId(), command.deviceId(), rememberMe);

        return new AuthenticationResult(accessToken, refreshToken, rememberMe);
    }

    @Transactional
    public AuthenticationResult refreshAccessToken(RefreshAccessTokenCommand command) {
        Token oldRefreshToken = tokenService.validateRefreshTokenForRotation(command.tokenValue(), command.deviceId());

        User user = userService.findEnabledUser(oldRefreshToken.getUserId());

        var newAccessToken = jwtService.createAccessToken(user);
        var newRefreshToken = tokenService.rotateRefreshToken(oldRefreshToken, oldRefreshToken.getDeviceId());

        return new AuthenticationResult(newAccessToken, newRefreshToken, oldRefreshToken.isRememberMe());
    }

    @Transactional
    public void logout(LogoutCommand command) {
        String deviceId = command.deviceId();
        try {
            tokenService.validateAndRevokeRefreshToken(command.refreshToken(), deviceId);
        }
        catch (InvalidTokenException | TokenReuseException e) {
            log.warn("Refresh token reuse detected during logout with deviceId: {}", deviceId);
        }
    }

    @Transactional
    public void verifyEmail(VerifyOtpCommand command) {
        User user = userService.findUnverifiedUser(command.email());
        tokenService.verifyAndUseToken(user.getId(), command.tokenValue(), TokenType.EMAIL_VERIFY);
        userService.enableUser(user);
    }

    @Transactional
    public void resendVerificationEmail(OtpCommand command) {
        User user = userService.findUnverifiedUser(command.email());
        User updatedUser = userService.validateAndAdvanceCooldown(user, TokenType.EMAIL_VERIFY);
        String token = tokenService.issueResendVerificationToken(updatedUser.getId());

        applicationEventPublisher.publishEvent(
                new VerifyEmailRequestedEvent(user.getFullName(), user.getEmail(), token)
        );
    }

    @Transactional
    public void forgotPassword(OtpCommand command) {
        try {
            User user = userService.findEnabledUser(command.email());
            User updatedUser = userService.validateAndAdvanceCooldown(user, TokenType.PASSWORD_RESET);
            String token = tokenService.issuePasswordResetToken(updatedUser.getId());

            applicationEventPublisher.publishEvent(
                    new PasswordResetRequestedEvent(user.getFullName(), user.getEmail(), token)
            );
        } catch (ResourceNotFoundException | UserNotVerifiedException | TokenCooldownException e) {
            log.info("Password reset request could not be processed. Reason: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public String verifyPasswordReset(VerifyOtpCommand command) {
        User user = userService.findEnabledUser(command.email());
        tokenService.verifyAndUseToken(user.getId(), command.tokenValue(), TokenType.PASSWORD_RESET);
        return tokenService.issuePasswordResetVerifiedToken(user.getId());
    }

    @Transactional
    public void resetPassword(PasswordResetCommand command) {
        String resetToken = command.resetToken();
        Token token = tokenService.validatePasswordResetVerifiedToken(resetToken);
        tokenService.verifyAndUseToken(token.getUserId(), resetToken, TokenType.PASSWORD_RESET_VERIFIED);
        User user = userService.findEnabledUser(token.getUserId());
        User updatedUser = userService.updateUserPassword(user, command.newPassword());
        tokenService.revokeAllRefreshTokens(updatedUser.getId());

        applicationEventPublisher.publishEvent(
                new PasswordResetConfirmedEvent(updatedUser.getFullName(), updatedUser.getEmail())
        );
    }
}
