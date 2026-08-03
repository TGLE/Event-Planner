package com.tgle.planner.core.security;

import com.tgle.planner.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInitializer implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        userService.registerAdmin("John", "Admin", "JohnAdmin@gmail.com", "admin123");
    }
}
