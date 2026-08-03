package com.tgle.planner.core.security;

import com.tgle.planner.user.infrastructure.security.UserPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

public class CustomJwtAuthenticationToken extends AbstractAuthenticationToken {

    private final UserPrincipal principal;
    private final Jwt jwt;

    public CustomJwtAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities, UserPrincipal principal) {
        super(authorities);
        this.jwt = jwt;
        this.principal = principal;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return jwt.getTokenValue();
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
