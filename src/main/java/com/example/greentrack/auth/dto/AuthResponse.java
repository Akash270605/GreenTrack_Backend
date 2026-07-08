/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * A DTO (Data Transfer Object) for Authentication responses.
 * After a successful login or registration, the server sends back an AuthResponse
 * containing a JWT token and user info.
 */
package com.example.greentrack.auth.dto;

import com.example.greentrack.auth.Role;

/**
 * AuthResponse: This is the JSON structure sent back to the client when 
 *                  a user or logs is successfully.
 * 
 * Fields:
 *      token       -> A JWT (JSON Web Token) string. 
 *      userId      -> The unique database ID of the user.
 *      tenantId  -> The tenant/organization this user belongs to.
 *      fullName  -> The user's full name for display purposes.
 *      email        -> The user's email address.
 *      role           -> the user's role (ADMIN, ANALYST, etc.)
 */
public record AuthResponse(
        String token,
        Long userId,
        String tenantId,
        String fullName,
        String email,
        Role role
        ) {
    
}
