package com.suryakn.IssueTracker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Configuration de sécurité pour l'application IssueTracker.
 * 
 * Cette configuration applique les règles suivantes :
 * - Les endpoints d'authentification (/api/auth/**) sont accessibles publiquement
 * - Les ressources statiques (screenshots) sont accessibles publiquement
 * - Le stream SSE des notifications nécessite une authentification
 * - Toutes les autres requêtes API nécessitent une authentification JWT valide
 * - La session est stateless (pas de session HTTP)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // Désactiver CSRF pour une API stateless
                .csrf(AbstractHttpConfigurer::disable)
                
                // Configuration CORS
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                
                // Configuration des autorisations
                .authorizeHttpRequests(authorize -> authorize
                        // Endpoints publics d'authentification
                        .requestMatchers("/api/auth/**").permitAll()
                        
                        // Ressources statiques (screenshots)
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/static/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/api/screenshots/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        
                        // Notification endpoints (SSE doesn't support auth headers)
                        .requestMatchers("/api/v1/notifications/health").permitAll()
                        .requestMatchers("/api/v1/notifications/stream").permitAll()
                        .requestMatchers("/api/v1/notifications").permitAll()
                        .requestMatchers("/api/v1/notifications/unread-count").permitAll()
                        
                        // Chat API (public for demo, can be protected)
                        .requestMatchers("/api/chat/**").permitAll()
                        .requestMatchers("/api/classification/**").permitAll()
                        
                        // SLA endpoints (public access for dashboard)
                        .requestMatchers("/api/sla/**").permitAll()
                        .requestMatchers("/api/v1/sla/**").permitAll()
                        
                        // FAQ and Knowledge endpoints (public access)
                        .requestMatchers("/api/v1/faqs/**").permitAll()
                        .requestMatchers("/api/v1/unanswered-questions/**").permitAll()
                        .requestMatchers("/api/v1/departments/**").permitAll()
                        
                         // WebSocket et SSE (si nécessaire sans auth)
                         .requestMatchers("/ws/**").permitAll()
                         
                        // Toutes les autres requêtes nécessitent une authentification
                        .anyRequest().authenticated())
                
                // Gestion de session stateless
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Configuration de l'authentification
                .authenticationProvider(authenticationProvider)
                
                // Ajout du filtre JWT avant le filtre de connexion
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                
                // Gestion des exceptions (optionnel, peut être externalisé)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(401);
                            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json");
                            response.setStatus(403);
                            response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"" + accessDeniedException.getMessage() + "\"}");
                        }));
        
        return httpSecurity.build();
    }
    
    /**
     * Configuration CORS pour autoriser les requêtes depuis le frontend
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                
                // Origines autorisées (frontend)
                config.setAllowedOrigins(Arrays.asList(
                    "http://localhost:5173", 
                    "http://localhost:5174", 
                    "http://localhost:3000",
                    "http://127.0.0.1:5173",
                    "http://127.0.0.1:5174",
                    "http://127.0.0.1:3000",
                    "http://localhost:5000",
                    "http://localhost:5001"
                ));
                
                // Méthodes HTTP autorisées
                config.setAllowedMethods(Arrays.asList(
                    "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
                ));
                
                // Headers autorisés
                config.setAllowedHeaders(Arrays.asList(
                    "Content-Type", 
                    "Authorization", 
                    "Accept", 
                    "Cache-Control", 
                    "X-Requested-With", 
                    "Origin",
                    "Access-Control-Request-Method",
                    "Access-Control-Request-Headers",
                    "multipart/form-data"
                ));
                
                // Headers exposés (peuvent être lus par le navigateur)
                config.setExposedHeaders(Arrays.asList(
                    "Authorization", 
                    "Content-Disposition",
                    "X-Total-Count",
                    "X-Page-Count"
                ));
                
                // Autoriser l'envoi de cookies/credentials
                config.setAllowCredentials(true);
                
                // Durée de cache des préflight (en secondes)
                config.setMaxAge(3600L);
                
                return config;
            }
        };
    }
}