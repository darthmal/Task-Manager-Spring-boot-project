package com.salam.task_manager.services;

import com.salam.task_manager.models.user.User;
import com.salam.task_manager.repository.UserRepository;
import com.salam.task_manager.repository.mongodb.MongoDbUserRepository;
import com.salam.task_manager.repository.postgresql.PostgresqlUserRepository;
import com.salam.task_manager.utils.ProfileNameProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private final ProfileNameProvider profileNameProvider;

    public UserService(ProfileNameProvider profileNameProvider) {
        this.profileNameProvider = profileNameProvider;
    }

    @Autowired
    public void setUserRepository(List<UserRepository> repositories) {
        String activeProfile = profileNameProvider.getActiveProfileName();

        this.userRepository = repositories.stream()
                .filter(repository ->
                        (activeProfile.equals("mongodb") && repository instanceof MongoDbUserRepository) ||
                                (activeProfile.equals("postgresql") && repository instanceof PostgresqlUserRepository)
                )
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No user repository found for active profile: " + activeProfile));
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    public Optional<User> finUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
