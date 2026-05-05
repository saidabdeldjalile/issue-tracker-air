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
                    .registrationNumber("ADMIN-000")
                    .build();
            
            userRepository.save(admin);
            log.info("Default admin user created: admin@airalgerie.dz / admin123");
        }
        
        // Create super admin2 if not exists (email: admin2@airalgerie.dz, password: admin123)
        Optional<UserEntity> admin2Exists = userRepository.findByEmail("admin2@airalgerie.dz");
        if (admin2Exists.isEmpty()) {
            UserEntity admin2 = UserEntity.builder()
                    .email("admin2@airalgerie.dz")
                    .firstName("Admin")
                    .lastName("Global2")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .registrationNumber("ADMIN-002")
                    .build();
            userRepository.save(admin2);
            log.info("Super admin2 created: admin2@airalgerie.dz / admin123");
        }

        // Update admin@airalgerie.dz password to "admin123" if exists
        Optional<UserEntity> originalAdminExists = userRepository.findByEmail("admin@airalgerie.dz");
        if (originalAdminExists.isPresent()) {
            UserEntity admin = originalAdminExists.get();
            admin.setPassword(passwordEncoder.encode("admin123"));
            userRepository.save(admin);
            log.info("Admin admin@airalgerie.dz password updated to: admin123");
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
                    .registrationNumber("USER-000")
                    .build();

            userRepository.save(user);
            log.info("Default user created: user@airalgerie.dz / user123");
        }
    }
}