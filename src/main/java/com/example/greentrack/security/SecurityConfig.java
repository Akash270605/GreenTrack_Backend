/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * This is the Main Security Configuration for the application.
 * 
 * It defines:
 *      1. Which URLs are public (no authentication required).
 *      2. Which URLs require authentication.
 *      3. How authentication works (JWT filter, stateless sessions).
 *      4. CORS settings (who can call the API from the browsers).
 *      5. CSRF protection (disabled for REST APIs).
 * 
 * @EnableWebSecurity       -> Enables Spring Security's web security support.
 * @EnableMethodSecurity -> Enables @PreAuthorize annotations on controller methods.
 */
package com.example.greentrack.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity          // Activates Spring Security for web apps.
@EnableMethodSecurity    // Enables @PreAuthorize on controller methods.
public class SecurityConfig {
    
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    
    public SecurityConfig(JWTAuthenticationFilter jwtAuthenticationFilter){
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    
    /**
     * securityFilterChain(): The main security filter chain that process 
     * every HTTP request.
     * 
     * http                                     -> The HttpSecurity object (configured by Spring).
     * authenticationProvider -> The provider definer in SecurityBeansConfig.
     * 
     * Returns the configured SecurityFilterChain.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
                HttpSecurity http,
                AuthenticationProvider authenticationProvider
    ) throws Exception{
        
        return http
                // 1. Disable CSRF (Cross-Site Request Forgery) protection.
                // CSRF uses cookies for authentication (not needed here since tokens used).
                .csrf(csrf -> csrf.disable())
                
                //2. Enable CORS (Cross-Origin Resource Sharing).
                // Allows frontend apps on different domains to call this API.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // 3. Set session management to STATELESS.
                // We donn't create HTTP sessions; every request is authenticated
                // individually via JWT.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 4. Configure authorization rules.
                .authorizeHttpRequests(auth -> auth
                        
                        // Public endpoints (no token required):
                        //      POST /api/v2/auth/register  -> Create account
                        //      POST /api/v2/auth/login       -> Login
                        .requestMatchers(HttpMethod.POST, "/api/v2/auth/register", "/api/v2/auth/login").permitAll()
                        
                        // Swagger/OpenAPI docs & health check
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/actuator/health"
                        ).permitAll()
                        
                        // All other endpoints require authentication.
                        .anyRequest().authenticated()
                )
                
                // 5. Register our authentication provider
                .authenticationProvider(authenticationProvider)
                
                // 6. Add our JWT filter Before Spring's default
                // UsernamePasswordAuthenticationFilter.
                // This ensures the JWT is processed first
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                
                .build();
    }
    
    /**
     * corsConfigurationSource(): Defines CORS (Cross-Origin Resource Sharing) settings. 
     * CORS controls which websites can call this API from a browser.
     */
    @Bean 
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));              // Allow 
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-type"));
        
        // Apply this configuration to all paths.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
