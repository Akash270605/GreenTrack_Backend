/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * JWT stands for JSON Web Token. It's a secure way to transmit information
 * b/w parties as a JSON object.
 * 
 * When a user logs in, the server creates a JWT and sends it to the client.
 * The client then includes this token in the "Authorization" header for all
 * subsequent requests, proving their identity without needing to send the
 * password again.
 * 
 * A JWT contains:
 *      - Header (xxxxx): Contains metadata (algorithm used).
 *      - Payload (yyyyy): Contains the claims (user data).
 *      - Signature (zzzzz): Verifies the token hasn't been tampered with.
 */
package com.example.greentrack.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * JwtService handles all JWT - related operations:
 *      - Generating tokens (on login/register).
 *      - Extracting data from tokens (like username).
 *      - Validating tokens (checking expiry + signature).
 * 
 * How it works:
 *      - The JWT is signed with a Secret Key (defined in application.properties).
 *      - Only the server knows this key, so no one else can forge a token.
 *      - Each token has an Expiration Time (default: 24 hours).
 */
@Service
public class JWTService {
    
    // Secret key for signing JWTs (loaded from config).
    @Value("${greentrack.security.jwt.secret}")
    private String jwtSecret;
    
    // Token expiration time in milliseconds (loaded from the config).
    @Value("${greentrack.security.jwt.expiration-ms}")
    private long expirationMs;
    
    /**
     * extractUsername(): extracts the "subject" (which is the user's email) from a JWT.
     */
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * extractClaim(): A generic method to extract any claim (data field) from a JWT.
     * "Claims" are the key-value pairs stored in the JWT payload.
     * 
     * claimsResolver -> A function that tells How to extract the desired claim from
     *                          the Claim object.
     * <T>                     -> The type of the claim value.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        return claimsResolver.apply(extractAllClaims(token));
    }
    
    /**
     * generateToken(): Creates a new JWT for a user.
     * 
     * What goes inside the token:
     *      - Claims: tenantId, role, userId (custom data).
     *      - Subject: The user's email (who is this token for?).
     *      - Issued At: When the token was created.
     *      - Expiration: When the token expires.
     */
    public String generateToken(User user){
        
        // Create custom claims (extra data) to embed in the token.
        Map<String, Object> claims = new HashMap<>();
        claims.put("tenantId", user.getTenantId());
        claims.put("role", user.getRole().name());
        claims.put("userId", user.getId());
        
        // Build the JWT using the JJWT library.
        return Jwts.builder()
                .setClaims(claims)                                              // Custom data
                .setSubject(user.getEmail())                            // Who the token is for
                .setIssuedAt(new Date())                                  // Creation timestamp
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))    // Expiry
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)    // Sign with HMAX-SHA256
                .compact();                                                           // Convert to string
    }
    
    /**
     * isTokenValid(): Checks whether a JWT is still valid.
     * 
     * Conditions:
     *      1. The username inside the token matches the user's username.
     *      2. The token has not expired.
     * 
     * Returns true if the token is valid, false otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails){
        return extractUsername(token).equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }
    
    /**
     * isTokenExpired(): Checks if the token's expiration date has passed.
     * 
     * Returns true if expired, false if still valid.
     */
    private boolean isTokenExpired(String token){
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
    
    /**
     * extractAllClaims(): Parses the JWT and extracts all claims (the payload data).
     * 
     * Steps:
     *      1. Create a JWT parser.
     *      2. Provides the singing key (so it can verify the signature).
     *      3. Parse the token -> get the body (claims).
     */
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())     // Key to verify signature
                .build()
                .parseClaimsJws(token)                      // Parse and verify
                .getBody();                                             // Get the payload
    }       
    
    /**
     * getSigningKey(): Converts the Base64-encoded secret string into a key object
     * that the JJWT library can use for singing / verification
     * 
     * Returns a Key (HMAC-SHA) used to sing JWT tokens
     */
    private Key getSigningKey(){
        
        // Decode the Base64 string into raw bytes.
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        
        // Create an HMAC-SHA key from the bytes.
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
