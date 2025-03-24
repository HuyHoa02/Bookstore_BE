package com.chris.bookstore.configuration;


import com.chris.bookstore.entity.User;
import com.chris.bookstore.enums.Role;
import com.chris.bookstore.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;

    public  ApplicationInitConfig(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository
    ){
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    static final String ADMIN_USER_NAME = "admin";

    static final String ADMIN_PASSWORD = "admin";

    static final String ADMIN_EMAIL = "admin";


    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername(ADMIN_USER_NAME) == null) {
                User user = new User();
                user.setUsername(ADMIN_USER_NAME);
                user.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
                user.setRole(Role.ADMIN);
                user.setEmail(ADMIN_EMAIL);

                userRepository.save(user);
            }
        };
    }
}