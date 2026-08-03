package com.tgle.planner.authentication.presentation;

import com.tgle.planner.authentication.presentation.dto.AuthenticationResponse;
import com.tgle.planner.authentication.application.dto.AuthenticationResult;
import com.tgle.planner.authentication.presentation.dto.LoginRequest;
import com.tgle.planner.authentication.presentation.dto.RegisterRequest;
import com.tgle.planner.authentication.presentation.dto.OtpRequest;
import com.tgle.planner.authentication.presentation.dto.VerifyOtpRequest;
import com.tgle.planner.authentication.presentation.dto.PasswordResetRequest;
import com.tgle.planner.authentication.application.AuthenticationService;
import com.tgle.planner.core.dto.ApiResponse;
import com.tgle.planner.core.security.CookieService;
import com.tgle.planner.token.domain.TokenType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationMapper mapper;
    private final AuthenticationService authenticationService;
    private final CookieService cookieService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        authenticationService.register(mapper.toCommand(request));
        return ResponseEntity.ok(ApiResponse.of("Verification code has been sent. Please verify your email"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request,
            @RequestHeader("X-Device-Id") String deviceId
    ) {
        AuthenticationResult result = authenticationService.login(mapper.toCommand(request, deviceId));
        ResponseCookie cookie = cookieService.createRefreshTokenCookie(result.refreshToken(), result.rememberMe());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthenticationResponse(result.accessToken()));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @Valid @RequestBody VerifyOtpRequest request
    ) {
        authenticationService.verifyEmail(mapper.toCommand(request));
        return ResponseEntity.ok(ApiResponse.of("Your email has been successfully verified"));
    }

    @PostMapping("/resend-verification-email")
    public ResponseEntity<ApiResponse<Void>> resendVerificationEmail(
            @Valid @RequestBody OtpRequest request
    ) {
        authenticationService.resendVerificationEmail(mapper.toCommand(request));
        return ResponseEntity.ok(ApiResponse.of("New verification code has been sent. Please verify your email"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshAccessToken(
            @RequestHeader("X-Device-Id") String deviceId,
            @CookieValue(name = "${security.token.refresh.cookie-name}") String refreshToken
    ) {
        AuthenticationResult result = authenticationService
                .refreshAccessToken(mapper.toRefreshCommand(refreshToken, deviceId));
        ResponseCookie cookie = cookieService.createRefreshTokenCookie(result.refreshToken(), result.rememberMe());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthenticationResponse(result.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "X-Device-Id", required = false) String deviceId,
            @CookieValue(name = "${security.token.refresh.cookie-name}", required = false) String refreshToken
    ) {
        if (refreshToken != null) {
            authenticationService.logout(mapper.toLogoutCommand(refreshToken, deviceId));
        }
        ResponseCookie cookie = cookieService.removeTokenCookie(TokenType.REFRESH);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody OtpRequest request
    ) {
        authenticationService.forgotPassword(mapper.toCommand(request));
        return ResponseEntity.ok(ApiResponse.of(
                "If an account exists with this email, a password reset code has been sent"
        ));
    }

    @PostMapping("/verify-password-reset")
    public ResponseEntity<Void> verifyPasswordReset(
            @RequestBody @Valid VerifyOtpRequest request
    ) {
        String token = authenticationService.verifyPasswordReset(mapper.toCommand(request));
        ResponseCookie cookie = cookieService.createTokenCookie(token, TokenType.PASSWORD_RESET_VERIFIED);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @RequestBody @Valid PasswordResetRequest request,
            @CookieValue(name = "${security.token.password-reset-verified.cookie-name}") String resetToken
    ) {
        authenticationService.resetPassword(mapper.toCommand(request, resetToken));
        ResponseCookie refreshCookie = cookieService.removeTokenCookie(TokenType.REFRESH);
        ResponseCookie resetVerifiedCookie = cookieService.removeTokenCookie(TokenType.PASSWORD_RESET_VERIFIED);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString(), resetVerifiedCookie.toString())
                .build();
    }
}
