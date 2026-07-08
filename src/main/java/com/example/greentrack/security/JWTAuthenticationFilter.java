/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * This is a Filter that runs before every HTTP request.
 * 
 * A "filter" is web application intercepts incoming requests and outgoing 
 * responses. This particular filter checks if the request has a valid JWT token
 * in the Authorization header.
 * 
 * If the token is valid, it sets up the user's authentication in Spring Security's
 * SecurityContext so that the rest of the application knows who made the request.
 * 
 * extends OncePerRequestFilter: Ensures this filter runs exactly Once per request
 *                          (even if the request goes through multiple filter chains).
 */
package com.example.greentrack.security;

import com.example.greentrack.auth.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWTAuthenticationFilter: Intercepts every HTTP request and checks for a JWT token.
 * If a valid token is found, it authenticates the user for the current request.
 * 
 * How it works:
 *      1. Extract the "Authorization" header from the request.
 *      2. If it starts with "Bearer ", extracts the JWT token.
 *      3. Extract the username (email) from the token.
 *      4. If the user is not already authenticated, look up the user from the 
 *          database and verify the token.
 *      5. If valid, create an Authentication object and set it in the SecurityContext.
 *      6. Continue the filter chain (let the request proceed to the controller).
 */

@Component      // Make this a Spring-managed bean (auto-detected)
public class JWTAuthenticationFilter extends OncePerRequestFilter{
    
    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;
    
    public JWTAuthenticationFilter(JWTService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException{
        
        // Step 1: Get the Authorization header.
        String authHeader = request.getHeader("Authorization");
        
        // Step 2: If no header or not a Bearer token, skip filtering.
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);        // Continue without auth
            return;
        }
        
        // Step 3: Extract the JWT (remove "Bearer " prefix).
        // "Bearer " is 7 characters long so the token starts at index 7.
        String jwt = authHeader.substring(7);
        
        // Step 4: Extract the username (email) from the token.
        String username = jwtService.extractUsername(jwt);
        
        
        // Step 5: If we have a username and user is not already authenticated...
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            
            // Load user details from the database.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // verify the token is still valid for this user.
            if(jwtService.isTokenValid(jwt, userDetails)){
                
                // Create an authenticated token with the user's details and roles.
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                                userDetails,                                    // Principal: the user
                                null,                                                  // Credentials: null (already authenticated)
                                userDetails.getAuthorities()      // Roles/permission
                        );
                
                // Add request details (IP address, session ID, etc.)
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Set the authentication in the SecurityContext.
                // This tells Spring Security: "This user is authenticated for this request"
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        // Step 6: Continue the filter chain (proceed to the controller).
        filterChain.doFilter(request, response);
    }
}
