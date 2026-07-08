/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * This is Service layer for authentication operations.
 * 
 * AuthService handles the core login for:
 *      - Registering a new user
 *      - Logging in an existing user
 *      - Converting a User entity into an AuthResponse DTO
 */
package com.example.greentrack.auth;

import com.example.greentrack.auth.dto.AuthResponse;
import com.example.greentrack.auth.dto.LoginRequest;
import com.example.greentrack.auth.dto.RegisterRequest;
import com.example.greentrack.common.ConflictException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 *  Dependencies (injected via constructor):
 *      - UserRepository        -> To save/find users in the database.
 *      - PasswordEncoder   -> To hash passwords (BCrypt).
 *      - JwtService                 -> To generate JWT tokens.
 *      - AuthenticationManager -> To verify login credentials.
 */

@Service        // Marks this class as Spring Service (business logic bean)
public class AuthService {
    
    // Logger for recording events (like new registrations) to the console/log file.
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    
    // constructor injections: Spring automatically provides these dependencies.
    public AuthService(
    UserRepository userRepository,
    PasswordEncoder passwordEncoder,
    JWTService jwtService,
    AuthenticationManager authenticationManager
) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
}
    
    
    /**
     * register() : Creates a new user account in the database.
     * 
     * Steps:
     *      1. Check if the email is already taken -> throw ConflictException if yes.
     *      2. Create a new User object and populate its fields.
     *      3. Hash the password (Never store plaintext).
     *      4. Save the user to the database.
     *      5. Return an AuthResponse with a JWT token.
     * 
     * @Transactional -> This entire methods runs inside a database transaction.
     *                                          If something fails, all changes are rolled back.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request ){
        
        // Step 1: Check for duplicate email
        // existsByEmail() runs a Count query - returns true if email exists
        if(userRepository.existsByEmail(request.email())){
            throw new ConflictException("Email is already registered");
        }
        
        // Step 2: Build the user entity
        User user = new User();
        user.setTenantId(request.tenantId());
        user.setFullName(request.fullName());
        user.setEmail(request.email().toLowerCase());       // store email in lowercase for consistency
        user.setPasswordHash(passwordEncoder.encode(request.password()));   // BCrypt hash
        user.setRole(request.role());
        
        // Step 3: Save to the database
        User saved = userRepository.save(user);
        
        // Step 4: Log the event
        log.info("Registered user {} for tenant {} with role {}",
                    saved.getEmail(), saved.getTenantId(), saved.getRole());
        
        // Step 5: Return AuthResponse (with JWT)
        return toResponse(saved);
    }
    
    
    /**
     * login(): Authenticates user and returns a JWT token.
     * 
     * Steps:
     *      1. AuthenticationManager.authenticate() verifies the email + password
     *          against the database. If invalid, it throws BadCredentialsException.
     *      2. Fetch the full User object from the database.
     *      3. Return an AuthResponse with a JWT token.
     */
    public AuthResponse login(LoginRequest request){
        
        // Spring Security handles credential verification.
        // UsernamePasswordAuthenticationToken is a standard token type 
        //      that holds the principal (email) and credentials (password).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                            request.email().toLowerCase(),      // principal
                            request.password()                            // credentials
                )
        );
        
        // If we get here, authentication succeeded.
        // Fetch the user from DB to build the response.
        User user = userRepository.findByEmail(request.email().toLowerCase())
                    .orElseThrow();       // Should never happen since auth succeeded.
        
        return toResponse(user);
    }
    
    /**
     * toResponse() : Converts a User entity into an AutoResponse DTO.
     * This method generates a fresh JWT token for the user.
     */
    public AuthResponse toResponse(User user){
        return new AuthResponse(
                jwtService.generateToken(user),         // Generate JWT
                user.getId(),
                user.getTenantId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
