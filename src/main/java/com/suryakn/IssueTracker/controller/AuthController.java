package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "API d'authentification et gestion de mot de passe")
public class AuthController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    @Operation(summary = "Demander la réinitialisation du mot de passe")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email requis"));
        }
        
        PasswordResetService.PasswordResetResult result = passwordResetService.requestPasswordReset(email);
        
        return ResponseEntity.ok(Map.of("message", result.getMessage()));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Réinitialiser le mot de passe avec le token")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token requis"));
        }
        
        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "Le mot de passe doit contenir au moins 6 caractères"));
        }
        
        PasswordResetService.PasswordResetResult result = passwordResetService.resetPassword(token, newPassword);
        
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(Map.of("message", result.getMessage()));
        }
        
        return ResponseEntity.ok(Map.of("message", result.getMessage()));
    }

    @GetMapping("/validate-reset-token")
    @Operation(summary = "Valider le token de réinitialisation")
    public ResponseEntity<Map<String, Boolean>> validateResetToken(@RequestParam String token) {
        boolean valid = passwordResetService.validateToken(token);
        return ResponseEntity.ok(Map.of("valid", valid));
    }
}