package com.salam.task_manager.services;

import com.salam.task_manager.models.user.User;
import com.salam.task_manager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    public Optional<User> finUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
