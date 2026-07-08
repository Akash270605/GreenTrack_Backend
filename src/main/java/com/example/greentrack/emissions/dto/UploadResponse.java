/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

/**
 * A DTO returned after a file upload (CSV/Excel import). Provides a 
 * summary of the import results.
 */
package com.example.greentrack.emissions.dto;

import java.util.List;

/**
 * UploadResponse:  Response body for POST /api/v2/emissions/upload.
 * Tells the client how many rows were imported successfully, how many
 * were rejected, and details about each rejections.
 * 
 * Fields:
 *      importedRecords     -> No. of rows that were successfully saved as emission
 *                                  records.
 *      rejectedRecords       -> No. of rows that failed validation/parsing.
 *      errors                           -> List of UploadError objects detailing which rows
 *                                 failed and why.
 */
public record UploadResponse(
        int importedRecords,
        int rejectedRecords,
        List<UploadError> errors
        ) {

}
