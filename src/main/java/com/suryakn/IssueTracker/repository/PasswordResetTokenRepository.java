package com.suryakn.IssueTracker.repository;

import com.suryakn.IssueTracker.entity.PasswordResetToken;
import com.suryakn.IssueTracker.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUserAndUsedFalse(UserEntity user);
    void deleteByUserAndUsedTrue(UserEntity user);
}