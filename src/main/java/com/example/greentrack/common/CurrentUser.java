/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * A helper class that safely extracts the current authenticated user from the 
 * Spring Security's Authentication object.
 * 
 * It's marked with @Component so Spring can automatically discover and 
 * inject it into other classes that need it.
 */
package com.example.greentrack.common;

import com.example.greentrack.auth.User;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


// @Component -> Tells Spring to create a single instance (bean) of this
//                  class and mark it available for injection.
@Component
public class CurrentUser {
    
    /**
     * require(): Extracts the User from an Authentication object.
     * 
     * Steps:
     *      1. Check if authentication is null or the principal is not a User.
     *      2. If invalid, throw an IllegalArgumentException.
     *      3. If valid, cast and return the User.
     */
    public User require(Authentication authentication){
        
        // if (authentication is null) or (principal is not a User) -> throw.
        if(authentication == null || !(authentication.getPrincipal() instanceof User user)){
            throw new IllegalArgumentException("Authenticated user is required");
        }
        
        return user;        // Return the authenticated User object.
    }
}
