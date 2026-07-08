/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

/**
 * A DTO (Data Transfer Object) for Registration request.
 * 
 * When a new user wants to sign up, they send their details (tenantId,
 * fullName, email, password, role) in a JSON body.
 */
package com.example.greentrack.auth.dto;

import com.example.greentrack.auth.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * RegisterRequest:  Captures all required fields for creating a new user
 *                          account.
 * 
 * Fields:
 *      tenantId    -> Which organization/tenant this user belongs to.
 *      fullName   -> The user's display name.
 *      email         -> Login email (must be valid email format).
 *      password -> Minimum 8 characters, max 80.
 *      role            -> One of the Role enum values (ADMIN, ESG_MANAGER
 *                              ANALYST, VIEWER).
 * 
 * Validation Annotations:
 *      @NotBlank                           -> Fields cannot be null or empty/blank.
 *      @Size(max = 160)               -> Maximum string length.
 *      @Size(min=8, max = 80)   -> Password must be between 8-80 chars.
 *      @Email                                  -> Must be a valid email string.
 *      @NotNull                              -> Field cannot be null (used for enums/objects).
 */
public record RegisterRequest(
        @NotBlank String tenantId,
        @NotBlank @Size(max = 160) String fullName,
        @Email @NotBlank String email,
        @NotBlank @Size(min=8, max=80) String password,
        @NotNull Role role
        ) {

}
