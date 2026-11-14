package com.tgle.planner.user.domain;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserFactory {

    public User createDefault(String firstName, String lastName, String email, String password) {
        return User.create(firstName, lastName, email, password, List.of(Role.USER), false);
    }

    public User createAdmin(String firstName, String lastName, String email, String password) {
        return User.create(firstName, lastName, email, password, List.of(Role.ADMIN), true);
    }
}
