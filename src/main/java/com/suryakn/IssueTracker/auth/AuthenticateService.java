package com.suryakn.IssueTracker.auth;

import com.suryakn.IssueTracker.auth.dtos.AuthenticateRequest;
import com.suryakn.IssueTracker.auth.dtos.AuthenticationResponse;
import com.suryakn.IssueTracker.auth.dtos.RegisterRequest;
import com.suryakn.IssueTracker.config.JwtService;
import com.suryakn.IssueTracker.entity.Role;
import com.suryakn.IssueTracker.entity.UserEntity;
import com.suryakn.IssueTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticateService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticateService.class);
    
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if (!repository.findAllByEmail(request.getEmail()).isEmpty()) {
            throw new IllegalStateException("Email already registered");
        }

        if (repository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new IllegalStateException("Registration number already in use");
        }

        var user = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole()))
                .registrationNumber(request.getRegistrationNumber())
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticateRequest request) {
        logger.info("Attempting authentication for email: {}", request.getEmail());
        
        try {
            var usersWithEmail = repository.findAllByEmail(request.getEmail());
            
            if (usersWithEmail.isEmpty()) {
                logger.warn("User not found with email: {}", request.getEmail());
                throw new BadCredentialsException("Invalid email or password");
            }
            
            if (usersWithEmail.size() > 1) {
                logger.error("Multiple users found with email: {}. Found {} users", 
                    request.getEmail(), usersWithEmail.size());
                throw new BadCredentialsException("Multiple accounts found with this email. Please contact administrator.");
            }
            
            var user = usersWithEmail.get(0);
            logger.info("User found: {}, role: {}", user.getEmail(), user.getRole());
            
            if (!encoder.matches(request.getPassword(), user.getPassword())) {
                logger.warn("Password mismatch for user: {}", request.getEmail());
                throw new BadCredentialsException("Invalid email or password");
            }
            
            logger.info("Authentication successful for user: {}", user.getEmail());
            
            Long departmentId = null;
            if (user.getDepartment() != null) {
                try {
                    departmentId = user.getDepartment().getId();
                } catch (Exception e) {
                    logger.warn("Could not load department for user: {}", user.getEmail(), e);
                }
            }
            
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .role(user.getRole())
                    .departmentId(departmentId)
                    .build();
                    
        } catch (BadCredentialsException e) {
              throw e;
          } catch (Exception e) {
              logger.error("Unexpected error during authentication for email: {}", request.getEmail(), e);
              throw new BadCredentialsException("Authentication failed. Please try again later.");
          }
    }
}