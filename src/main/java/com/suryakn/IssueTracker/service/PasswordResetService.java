package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.dto.UserUpdateRequest;
import com.suryakn.IssueTracker.entity.PasswordResetToken;
import com.suryakn.IssueTracker.entity.UserEntity;
import com.suryakn.IssueTracker.repository.PasswordResetTokenRepository;
import com.suryakn.IssueTracker.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.password-reset.expiry-minutes:30}")
    private int tokenExpiryMinutes;

    @Data
    @Builder
    public static class PasswordResetResult {
        private boolean success;
        private String message;
        private String resetLink;
    }

    @Transactional
    public PasswordResetResult requestPasswordReset(String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            return PasswordResetResult.builder()
                    .success(true)
                    .message("Si l'adresse email existe, un lien de réinitialisation a été envoyé.")
                    .build();
        }

        UserEntity user = userOpt.get();
        
        tokenRepository.deleteByUserAndUsedTrue(user);
        
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(tokenExpiryMinutes))
                .used(false)
                .build();
        
        tokenRepository.save(resetToken);
        
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), token);
            log.info("Password reset email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.warn("Email not configured, returning reset link directly for testing. Token: {}", token);
            String resetLink = "/reset-password?token=" + token;
            return PasswordResetResult.builder()
                    .success(true)
                    .message("Lien de réinitialisation généré (email non configuré)")
                    .resetLink(resetLink)
                    .build();
        }
        
        return PasswordResetResult.builder()
                .success(true)
                .message("Si l'adresse email existe, un lien de réinitialisation a été envoyé.")
                .build();
    }

    @Transactional
    public PasswordResetResult resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        
        if (tokenOpt.isEmpty()) {
            return PasswordResetResult.builder()
                    .success(false)
                    .message("Token de réinitialisation invalide.")
                    .build();
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        
        if (!resetToken.isValid()) {
            return PasswordResetResult.builder()
                    .success(false)
                    .message("Le lien de réinitialisation a expiré ou a déjà été utilisé.")
                    .build();
        }
        
        UserEntity user = resetToken.getUser();
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        resetToken.setUsed(true);
        resetToken.setUsedAt(LocalDateTime.now());
        tokenRepository.save(resetToken);
        
        tokenRepository.deleteByUserAndUsedTrue(user);
        
        log.info("Password reset successful for user {}", user.getEmail());
        
        return PasswordResetResult.builder()
                .success(true)
                .message("Mot de passe réinitialisé avec succès.")
                .build();
    }

    public boolean validateToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        return tokenOpt.map(PasswordResetToken::isValid).orElse(false);
    }

    public Optional<UserEntity> getUserByToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(PasswordResetToken::isValid)
                .map(PasswordResetToken::getUser);
    }
}