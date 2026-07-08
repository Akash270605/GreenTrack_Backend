/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * This @Configuration class defines the Beans that Spring Security needs
 * to function.
 * 
 * A "bean" is an object that Spring manages (creates, injects, and destroys).
 * 
 * This class defines:
 *      1. UserDetailsService           -> How to load users from the database.
 *      2. AuthenticationProvider   -> How to verify credentials.
 *      3. AuthenticationManager   -> The main entry point for authentication.
 *      4. PasswordEncoder             -> How to hash/verify passwords (BCrypt).
 */
package com.example.greentrack.security;

import com.example.greentrack.auth.UserRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * This class creates and configures core Spring Security beans that are 
 * used for authentication.
 */
@Configuration
public class SecurityBeansConfig {
    
    /**
     * userDetailsService(): Defines how Spring Security looks up a user by 
     * their username (email).
     * 
     * @Bean -> Tells Spring to register the returned functions as a bean.
     * 
     * Returns a UserDetailsService that loads users from the database.
     */
    @Bean 
    public UserDetailsService userDetailsService(UserRepository userRepository){
        
        // This lamda takes a username (email) and returns User object.
        return username -> userRepository.findByEmail(username.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    
    /**
     * authenticationProvider(): Defines how authentication is performed.
     * DaoAuthenticationProvider is a built - in Spring provider that:
     *      1. Calls UserDetailsService to load the user.
     *      2. Uses PasswordEncoder to verify the password.
     * 
     * Returns a configured AuthenticationProvider.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(
        UserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
    
    /**
     * authenticationManager(): The central AuthenticationManager that coordinates authentication.
     * Spring boot provides a default one via AuthenticationConfiguration.
     */
    @Bean 
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception{
        
        return config.getAuthenticationManager();
    }
    
    /**
     * passwordEncoder(): Defines the password hashing algorithm. We use ByCrypt,
     *                      which is a strong, slow hashing algo. designed for passwords.
     * 
     * "BCrypt" automatically generates a random "salt" for each password
     * and stores it as part of the hash output.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
