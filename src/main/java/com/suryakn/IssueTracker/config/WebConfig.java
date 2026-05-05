package com.suryakn.IssueTracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.path:uploads/screenshots}")
    private String uploadPath;

    @Value("${app.cors.allowed-origins:http://localhost,http://localhost:80,http://localhost:5173,http://localhost:5174,http://localhost:3000,http://127.0.0.1,http://127.0.0.1:80,http://127.0.0.1:5173}")
    private String allowedOrigins;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadLocation;
        if (uploadPath.startsWith("/")) {
            uploadLocation = "file:" + uploadPath + "/";
        } else {
            Path uploadDir = Paths.get(uploadPath).toAbsolutePath();
            uploadLocation = uploadDir.toFile().toURI().toString();
        }

        registry.addResourceHandler("/api/screenshots/**")
                .addResourceLocations(uploadLocation)
                .setCachePeriod(3600);

        registry.addResourceHandler("/uploads/screenshots/**")
                .addResourceLocations(uploadLocation)
                .setCachePeriod(3600);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins.split(","))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "Cache-Control", "Accept", "Origin", "X-Requested-With")
                .exposedHeaders("Cache-Control", "Content-Type", "Authorization")
                .allowCredentials(true);
    }
}