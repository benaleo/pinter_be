package com.kopibery.pos.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT Authorization header using the Bearer scheme."
)
public class SwaggerConfig {

    @Value("${app.base.url}")
    private String baseUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server appServer = new Server();
        appServer.setUrl(baseUrl);
        appServer.setDescription("Development Server");

        return new OpenAPI()
                .servers(List.of(appServer));
    }

    // Define OpenAPI documentation group for the public API
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-api")
                .pathsToMatch("/api/**") // Only include APIs under '/api'
                .build();
    }

    // Define OpenAPI documentation group for the public API
    @Bean
    public GroupedOpenApi cmsApi() {
        return GroupedOpenApi.builder()
                .group("cms-api")
                .pathsToMatch("/cms/**") // Only include APIs under '/api'
                .build();
    }



    // Optionally, you can add more groups or customize API information
}
