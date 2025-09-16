package com.rag.service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "ApiKey";
        return new OpenAPI()
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName,
                    new SecurityScheme()
                        .name("X-API-Key")
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)))
            .info(new Info()
                .title("RAG Chat Storage API")
                .description("API for managing chat sessions, messages, and users with context-aware features")
                .version("1.0")
                .contact(new Contact()
                    .name("RAG Service Team")))
            .tags(Arrays.asList(
                new Tag().name("User Management").description("Operations for managing users"),
                new Tag().name("Chat Sessions").description("Operations for managing chat sessions"),
                new Tag().name("Session Chat Approach 2").description("Additional chat operations for session management"),
                new Tag().name("Health Check").description("API health check endpoints")
            ));
    }
}
