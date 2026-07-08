/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

/**
 * A DTO for creating/updating an emission record. The client sends this
 * in the request body for POST (create) and PUT (update) endpoints
 */
package com.example.greentrack.emissions.dto;

import com.example.greentrack.emissions.EmissionScope;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * EmissionRecordRequest: Request body for POST /api/v2/emissions/ and PUT /api/v2/emissions/{id}.
 * Contains all required and optional fields for an emission record.
 * 
 * Fields:
 *      facility                -> Where the emission occurred (required).
 *      businessUnit     -> The department/unit (required).
 *      scope                 -> Emission scope (required).
 *      activityType     -> Description of the activity (required).
 *      activityDate      -> Date of the activity (required).
 *      quantity             -> Amount of activity (required).
 *      unit                     -> Unit of the quantity (required).
 *      emissionFactorKey -> Optional: Key to look up a predefined factor.
 *      emissionFactor -> Optional: direct factor value.
 *      sourceDescription -> Optional: notes about the data source.
 */
public record EmissionRecordRequest(
        @NotBlank String facility,
        @NotBlank String businessUnit,
        @NotNull EmissionScope scope,
        @NotBlank String activityType,
        @NotNull LocalDate activityDate,
        @NotNull @DecimalMin("0.0") BigDecimal quantity,
        @NotBlank String unit,
        String emissionFactorKey,
        @DecimalMin("0.0") BigDecimal emissionFactor,
        String sourceDescription
        ) {

}
