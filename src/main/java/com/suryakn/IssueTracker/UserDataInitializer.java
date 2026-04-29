package com.suryakn.IssueTracker;

import com.suryakn.IssueTracker.entity.Role;
import com.suryakn.IssueTracker.entity.UserEntity;
import com.suryakn.IssueTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default admin user if not exists
        Optional<UserEntity> adminExists = userRepository.findByEmail("admin@airalgerie.dz");
        
        if (adminExists.isEmpty()) {
            UserEntity admin = UserEntity.builder()
                    .email("admin@airalgerie.dz")
                    .firstName("Admin")
                    .lastName("AirAlgérie")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            
            userRepository.save(admin);
            log.info("Default admin user created: admin@airalgerie.dz / admin123");
        }
        
        // Create default user
        Optional<UserEntity> userExists = userRepository.findByEmail("user@airalgerie.dz");
        
        if (userExists.isEmpty()) {
            UserEntity user = UserEntity.builder()
                    .email("user@airalgerie.dz")
                    .firstName("User")
                    .lastName("Test")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.USER)
                    .build();
            
            userRepository.save(user);
            log.info("Default user created: user@airalgerie.dz / user123");
        }
    }
}