package com.tgle.planner.user.application.service;

import com.tgle.planner.user.domain.User;
import com.tgle.planner.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStateService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public void enableUser(User user) {
        User enabledUser = user.enable();
        userRepository.save(enabledUser);
    }

    public User updateUserEmail(User user, String email) {
        User updatedUser = user.updateEmail(email);
        return userRepository.save(updatedUser);
    }

    public User updateUserPassword(User user, String newPassword) {
        User updatedUser = user.updatePassword(passwordService.encode(newPassword));
        return userRepository.save(updatedUser);
    }
}
