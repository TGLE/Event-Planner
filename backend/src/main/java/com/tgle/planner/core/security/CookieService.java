package com.tgle.planner.core.security;

import com.tgle.planner.token.infrastructure.component.TokenPropertiesResolver;
import com.tgle.planner.token.domain.TokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CookieService {

    private final TokenPropertiesResolver tokenProperties;

    public ResponseCookie createRefreshTokenCookie(String tokenValue, boolean rememberMe) {
        TokenType type = TokenType.REFRESH;
        Duration expiration = rememberMe
                ? tokenProperties.getExpiration(type, true)
                : Duration.ofSeconds(-1);
        String name = tokenProperties.getCookieName(type);
        return createCookie(name, tokenValue, expiration);
    }

    public ResponseCookie createTokenCookie(String tokenValue, TokenType type) {
        String name = tokenProperties.getCookieName(type);
        Duration expiration = tokenProperties.getExpiration(type);
        return createCookie(name, tokenValue, expiration);
    }

    public ResponseCookie removeTokenCookie(TokenType type) {
        String name = tokenProperties.getCookieName(type);
        return removeCookie(name);
    }

    public boolean hasCookie(HttpServletRequest request, TokenType type) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(tokenProperties.getCookieName(type))) {
                return true;
            }
        }
        return false;
    }

    private ResponseCookie createCookie(String name, String tokenValue, Duration expiration) {
        return buildBaseCookie(name, tokenValue)
                .maxAge(expiration)
                .build();
    }

    private ResponseCookie removeCookie(String name) {
        return buildBaseCookie(name, "")
                .maxAge(0)
                .build();
    }

    private ResponseCookie.ResponseCookieBuilder buildBaseCookie(String name, String tokenValue) {
        return ResponseCookie.from(name, tokenValue)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict");
    }
}
