/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

/**
 * A DTO (Data Transfer Object) for Login requests.
 * 
 * LoginRequest holds the data that a user sends when they try to login in
 * (email + password).
 * 
 * This is a Java "record" which is a concise way to create a class that only
 * holds data (automatically generates constructor, getters, equals, hashCode,
 * and toString()).
 */
package com.example.greentrack.auth.dto;

//Jakarta Validation annotations:
//      @Email          -> Ensures the field contains a valid email address format.
//      @NotBlank    -> Ensures the field is not null and has atleast one non-whitespace
//                                    character
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


/**
 * LoginRequest:    Represents the JSON body that a client must send to the 
 * "/api/auth/login" endpoint.
 * 
 * Fields:      
 *      email       -> The user's email address (used as username).
 *      password -> The user's password (in plain text; will be hashed/compared
 *                          with BCrypt internally).
 */
public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
        ) {

}
