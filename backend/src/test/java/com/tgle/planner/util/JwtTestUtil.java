package com.tgle.planner.util;

import com.tgle.planner.core.security.JwtService;
import com.tgle.planner.user.infrastructure.persistence.UserEntity;
import com.tgle.planner.user.infrastructure.persistence.UserPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTestUtil {

    private final JwtService jwtService;
    private final UserPersistenceMapper mapper;

    public String createAccessToken(UserEntity user) {
        return jwtService.createAccessToken(mapper.toDomainEntity(user));
    }
}
