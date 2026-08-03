package com.tgle.planner.user.application.service;

import com.tgle.planner.user.domain.User;
import com.tgle.planner.token.domain.UserFactory;
import com.tgle.planner.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreationService {

    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final PasswordService passwordService;

    public User createDefaultUser(String firstName, String lastName, String email, String password) {
        String hashedPassword = passwordService.encode(password);
        User user = userFactory.createDefault(firstName, lastName, email, hashedPassword);
        return userRepository.save(user);
    }

    public User createAdminUser(String firstName, String lastName, String email, String password) {
        String hashedPassword = passwordService.encode(password);
        User user = userFactory.createAdmin(firstName, lastName, email, hashedPassword);
        return userRepository.save(user);
    }
}
