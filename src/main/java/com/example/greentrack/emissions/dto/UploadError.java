/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

/**
 * A DTO representing a single error that occurred during bulk upload
 * (file import).
 */
package com.example.greentrack.emissions.dto;

/**
 *  UploadError: Stores information about a row that failed to import during a 
 *                  CSV/Excel file upload.
 * 
 * Fields:
 *      rowNumber -> The row number in the file where the error occured
 *                      (1-indexed, including the header row.)
 *      message       -> A description of what went wrong (e.g., "Missing required 
 *                                  column: facility")
 */
public record UploadError(
        int rowNumber,
        String message
        ) {

}
