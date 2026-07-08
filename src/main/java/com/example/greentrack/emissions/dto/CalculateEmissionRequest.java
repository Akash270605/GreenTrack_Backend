/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

/**
 * A DTO for the emission calculation endpoint. The client sends this to calculate CO2e 
 * emissions without saving a record.
 */
package com.example.greentrack.emissions.dto;

import com.example.greentrack.emissions.EmissionScope;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * CalculateEmissionRequest: Request body for POST /api/v2/emissions/calculate.
 * Contains the activity data needed to compute CO2e emissions.
 * 
 * Fields:  
 *      scope                   -> Which emission scope (SCOPE_1, SCOPE_2, SCOPE_3).
 *      quantity               -> The amount of activity (e.g., 500).
 *      unit                       -> The unit of quantity (e.g., "litre", "kWh").
 *      emissionFactorKey -> Optional: lookup key for a predefined factor.
 *      emissionFactors -> Optional: direct factor value (overrides lookup).
 */
public record CalculateEmissionRequest(
        @NotNull EmissionScope scope,
        @NotNull @DecimalMin("0.0") BigDecimal quantity,
        @NotBlank String unit,
        String emissionFactorKey,
        @DecimalMin("0.0") BigDecimal emissionFactor
        ) {

}
