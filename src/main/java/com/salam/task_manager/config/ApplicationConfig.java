package com.salam.task_manager.config;

import com.salam.task_manager.repository.UserRepository;
import com.salam.task_manager.repository.mongodb.MongoDbUserRepository;
import com.salam.task_manager.repository.postgresql.PostgresqlUserRepository;
import com.salam.task_manager.utils.ProfileNameProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
//@RequiredArgsConstructor
public class ApplicationConfig {

    private UserRepository userRepository;

    private final ProfileNameProvider profileNameProvider;

    public ApplicationConfig(ProfileNameProvider profileNameProvider) {
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

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
