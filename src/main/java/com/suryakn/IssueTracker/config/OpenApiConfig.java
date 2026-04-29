package com.suryakn.IssueTracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Air Algeria Issue Tracker API")
                        .version("2.0.0")
                        .description("API de gestion des demandes internes Air Algérie avec intégration IA")
                        .contact(new Contact()
                                .name("Air Algérie IT")
                                .email("it@airalgerie.dz"))
                        .license(new License()
                                .name("Internal Use Only")));
    }
}