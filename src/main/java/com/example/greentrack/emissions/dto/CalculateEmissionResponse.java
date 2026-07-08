/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

/**
 * A DTO for the emission calculation result. Sent back to the client after
 * a calculate request.
 */
package com.example.greentrack.emissions.dto;

import com.example.greentrack.emissions.EmissionScope;

import java.math.BigDecimal;

/**
 * CalculateEmissionResponse: Response body for POST /api/v2/emissions/calculate.
 * Contains the input parameters plus the calculated CO2e value.
 * 
 * Fields:
 *      scope               -> The emission scope.
 *      quantity           -> The activity quantity (echoed from request).
 *      unit                   -> The unit of measurement (echoed from request).
 *      emissionFactor -> The factor used in the calculation.
 *      co2e                 -> The calculated CO2e = quantity * emissionFactor
 */
public record CalculateEmissionResponse(
        EmissionScope scope,
        BigDecimal quantity,
        String unit,
        BigDecimal emissionFactor,
        BigDecimal co2e
        ) {

}
