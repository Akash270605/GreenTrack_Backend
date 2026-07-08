/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * This is Controller layer for authentication endpoints.
 * 
 * A "Controller" handles incoming HTTP requests and sends back HTTP
 * responses. It's the entry point for the REST API.
 * 
 * The @RestController annotation tells Spring that this class should be 
 * treated as a web controller where every method returns a JSON/object
 * response (not a view template)
 */
package com.example.greentrack.auth;

import com.example.greentrack.auth.dto.AuthResponse;
import com.example.greentrack.auth.dto.LoginRequest;
import com.example.greentrack.auth.dto.RegisterRequest;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 * EndPoints:
 *      POST /api/auth/register     -> Creates a new account.
 *      POST /api/auth/login          -> Log in and get a JWT.
 *      GET /api/auth/me                -> Get info about the currently logged-in user.
 */

@RestController                                 // Indicates this is a REST controller
@RequestMapping ("/api/v2/auth")   // Base path for all endpoints here.
public class AuthController {
    
    // Service layer injected via constructor.
    private final AuthService authService;
    
    // Constructor: Spring automatically provides the AuthService bean.
    public AuthController(AuthService authService){
        this.authService = authService;
    }
    
    
    /**
     * Creates a new user account.
     * 
     * @Valid -> Automatically validates the request body using the validation
     *                  annotations in RegisterRequest (e.g. @NotBlank). If validation fails
     *                  Spring returns a 400 Bad Request.
     * 
     * @RequestBody -> Tells Spring to deserialize the HTTP request body (JSON) into
     *                                   a RegisterRequest object.
     * 
     * @ResponseStatus(HttpStatus.CREATED) -> Returns HTTP 201 (created)
     *                                              instead of the default 200 (OK).
     * 
     * Returns AuthResponse with JWT and user details.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request){
        
        // Delegate to the service layer.
        return authService.register(request);
    }
    
    
    /**
     * Authenticates an existing user.
     *      request -> The login credentials (email + password).
     * 
     * Returns AuthResponse with JWT and user details.
     */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request){
        return authService.login(request);
    }
    
    
    /**
     * Returns the currently authenticated user's info.
     * 
     * Authentication: Spring Security automatically injects the Authentication object which
     *                              contains details about the logged-in user (the principal). This only works
     *                              if the request includes a valid JWT in the Authorization header.
     */
    @GetMapping("/me")
    public AuthResponse me(Authentication authentication){
        
        // authentication.getPrincipal() returns the User object that was stored in the 
        //          SecurityContext during the JWT validation.
        return authService.toResponse((User) authentication.getPrincipal());
    }
}
