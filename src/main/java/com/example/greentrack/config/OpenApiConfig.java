/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * This class configures Swagger/OpenAPI documentation.
 * 
 * Swagger (now called OpenAPI) automatically generates documentation for
 * the REST APIs. It creates a web page where we can see all endpoints, their parameters,
 * and even test them directly in the browser.
 * 
 * @Configuration  -> Marks this class a source of bean definitions. Spring will
 *                                    call the @Bean method to create objects that can be injected 
 *                                   elsewhere.
 */
package com.example.greentrack.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * OpenAPI documentation includes:
 *      - API title, version, and description.
 *      - Security scheme definition (JWT Bearer token).
 *      - Making every endpoint require the Bearer token by default.
 */

@Configuration
public class OpenApiConfig {
    
    /**
     * greenTrackOpenApi(): Creates and configure the main OpenAPI bean.
     * 
     * @Bean -> Tells Spring to register the returned object as a bean that can be
     *                  used by the OpenAPI library
     */
    @Bean
    public OpenAPI greenTrackOpenApi(){
        return new OpenAPI()
                
                // API metadata: title, version, description
                .info(new Info()
                        .title("GreenTrack API")
                        .version("v2")
                        .description("Backend APIs for emission, ESG dashboard, authentication, and reporting data."))
        
                // Add a global security requirement: all endpoints
                // require a Bearer token (unless explicitly overridden)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                
                // Define the security scheme: Bearer token (JWT).
                .components(new Components().addSecuritySchemes(
                        "bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)         // HTTP - based auth
                                .scheme("bearer")                                       // "Bearer" scheme
                                .bearerFormat("JWT")                                // Format is JWT
                ));
    }
}
