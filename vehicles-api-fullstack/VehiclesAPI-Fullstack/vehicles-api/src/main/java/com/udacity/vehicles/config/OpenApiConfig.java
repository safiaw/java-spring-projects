package com.udacity.vehicles.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Vehicles API",
                version = "1.0",
                description = "This API help perform CRUD operations on vehicles.",
                contact = @Contact(name = "Wahdat Safia", url = "https://www.udacity.com", email = "wahdat.safia@gmail.com"),
                license = @License(name = "License of API", url = "http://www.udacity.com/license")
        )
)
@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("vehicles-api")
                .pathsToMatch("/**")
                .build();
    }
}