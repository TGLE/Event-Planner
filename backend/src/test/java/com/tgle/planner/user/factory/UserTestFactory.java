package com.tgle.planner.user.factory;

import com.tgle.planner.user.domain.Role;
import com.tgle.planner.user.infrastructure.persistence.UserEntity;

import java.util.List;
import java.util.Map;

public final class UserTestFactory {

    private UserTestFactory() {}

    public static UserEntity createDefault() {
        return createDefault("JohnDoe@gmail.com");
    }

    public static UserEntity createDefault(String email) {
        return UserEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .email(email)
                .password("password")
                .roles(List.of(Role.USER))
                .locked(false)
                .enabled(true)
                .cooldowns(Map.of())
                .build();
    }
}
