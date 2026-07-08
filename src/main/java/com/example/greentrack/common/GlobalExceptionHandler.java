/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * This is a global Exception Handler for the entire application.
 * 
 * Instead of writing try-catch blocks in every controller method, we define
 * handlers here that catch specific exception types and return standardized JSON
 * error responses.
 * 
 * @RestControllerAdvice is a combination of:
 *      -@ControllerAdvice: Intercepts exception from all controllers.
 *      -@ResponseBody: Writes the return value directly to the HTTP response.
 */
package com.example.greentrack.common;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * How it works:
 *      Each @ExceptionHandler method is mapped to a specific exception type.
 *      When that exception is thrown, Spring calls the matching handler
 *      method automatically.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handles validation error (e.g., @NotBlank, @Email violations).
     * HTTP Status: 400 Bad Request.
     * 
     * This is thrown automatically by Spring when a @Valid request body fails
     * validation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex){
        
        // Collect all fields errors into a list of messages.
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)        // e.g.: "email: must be a valid email"
                .toList();
        
        return error(HttpStatus.BAD_REQUEST, "Validation failed", details);
    }
    
    /**
     * Handles illegal arguments (e.g. invalid input from user)
     * HTTP Status: 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> badRequest(IllegalArgumentException ex){
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of(ex.getMessage()));
    }
    
    /**
     * Handles "resource not found" exceptions.
     * HTTP Status: 404 Not Found
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> notFound(NotFoundException ex){
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), List.of(ex.getMessage()));
    }
    
    /**
     * Handles "conflict" exceptions (e.g. duplicate email)
     * HTTP Status: 490 conflict
     */
     
    
    /**
     * Handles bad credentials (Wrong email/password during login)
     * HTTP Status: 401 Unauthorized
     * 
     * Note: BadCredentialException is a RuntimException
     */
    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<ApiError> unauthorized(RuntimeException ex){
        return error(HttpStatus.UNAUTHORIZED, "Invalid credentials",
                List.of("Invalid email or password"));
    }
    
    /**
     * Handles access denied (user doesn't have the required role)
     * HTTP Status: 403 Forbidden
     * 
     * This is thrown automatically by @PreAuthorize annotation
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> forbidden(AccessDeniedException ex){
        return error(HttpStatus.FORBIDDEN, "Access denied",
                List.of("Your role cannot perform this operations"));
    }
    
    
    
    // ---------------- Private Helper Method --------------
    
    /**
     * Builds a standardized error ResponseEntity
     * 
     * status       -> The HTTP status (e.g. 400, 404, etc)
     * message -> A short error description.
     * details     -> A list of detailed error messages.
     * 
     * Returns the ResponseEntity containing the ApiError body.
     */
    private ResponseEntity<ApiError> error(HttpStatus status, String message, List<String> details){
        return ResponseEntity.status(status)
                .body(new ApiError(Instant.now(), status.value(), message, details));
    }
    
     // Formats a single field validation error into a readable string.
     private String formatFieldError(FieldError error){
         return error.getField() + ": " + error.getDefaultMessage();
     }
}
