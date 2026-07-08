/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

/**
 * A DTO for error responses.
 * 
 * When something goes wrong (validation fails, resource not found, etc.),
 * the server sends back a JSON error response to the client.
 * This record defines the structure of that error response.
 */
package com.example.greentrack.common;

import java.time.Instant;
import java.util.List;


/**
 * Fields:
 *      timestamp -> When the error occurred (UTC).
 *      status          -> HTTP status code (e.g, 400, 404, 409).
 *      error           -> A short error title/description.
 *      details        -> A list of detailed error message (e.g. which fields
 *                              failed validation).
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        List<String> details
        ) {

}
