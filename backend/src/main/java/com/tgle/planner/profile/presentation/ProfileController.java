package com.tgle.planner.profile.presentation;

import com.tgle.planner.core.dto.ApiResponse;
import com.tgle.planner.profile.presentation.dto.ChangePasswordRequest;
import com.tgle.planner.profile.application.ProfileService;
import com.tgle.planner.profile.presentation.dto.ChangeEmailRequest;
import com.tgle.planner.profile.presentation.dto.VerifyEmailChangeRequest;
import com.tgle.planner.core.security.CookieService;
import com.tgle.planner.token.domain.TokenType;
import com.tgle.planner.user.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileMapper mapper;
    private final ProfileService profileService;
    private final CookieService cookieService;

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        profileService.changePassword(mapper.toCommand(request, userPrincipal.getId()));
        ResponseCookie cookie = cookieService.removeTokenCookie(TokenType.REFRESH);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.of("Your password has been changed"));
    }

    @PatchMapping("/email")
    public ResponseEntity<Void> changeEmail(
            @RequestBody @Valid ChangeEmailRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        profileService.changeEmail(mapper.toCommand(request, userPrincipal.getId()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-email-change")
    public ResponseEntity<Void> verifyEmailChange(
            @RequestBody @Valid VerifyEmailChangeRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        profileService.verifyEmailChange(mapper.toCommand(request, userPrincipal.getId()));
        ResponseCookie cookie = cookieService.removeTokenCookie(TokenType.REFRESH);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        profileService.logoutAll(userPrincipal.getId());
        ResponseCookie cookie = cookieService.removeTokenCookie(TokenType.REFRESH);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
