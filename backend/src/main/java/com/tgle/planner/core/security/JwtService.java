package com.tgle.planner.core.security;

import com.tgle.planner.core.properties.SecurityProperties;
import com.tgle.planner.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final Clock clock;
    private final JwtEncoder jwtEncoder;
    private final SecurityProperties security;

    public String createAccessToken(Authentication authentication) {
        String subject = authentication.getName();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth != null && auth.startsWith("ROLE_"))
                .toList();

        return buildAccessToken(subject, roles);
    }

    public String createAccessToken(User user) {
        String subject = user.getEmail();

        List<String> roles = user.getRoles().stream()
                .map(role -> "ROLE_" + role.name())
                .toList();

        return buildAccessToken(subject, roles);
    }

    private String buildAccessToken(String subject, List<String> roles) {
        Instant now = Instant.now(clock);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(security.jwt().expiration()))
                .subject(subject)
                .claim("roles", String.join(" ", roles))
                .build();

        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256)
                .type("JWT")
                .build();

        var encoderParameters = JwtEncoderParameters.from(jwsHeader, claims);
        return jwtEncoder.encode(encoderParameters).getTokenValue();
    }
}
