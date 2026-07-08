/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

/**
 * A DTO sent back to the client after creating, updating or 
 * listing records.
 */
package com.example.greentrack.emissions.dto;

import com.example.greentrack.emissions.EmissionScope;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * EmissionRecordResponse: Represents the JSON structure of an emission record sent
 *                          to the frontend.
 * 
 * Fields:
 *      id                          -> Database ID of the record.
 *      facility                 -> Where the emission occurred.
 *      businessUnit      -> The department/unit.
 *      scope                  -> Emission scope.
 *      activityType      -> Description of the activity.
 *      activityDate       -> Date of the activity.
 *      quantity              -> Amount of the activity.
 *      unit                      -> Unit of the quantity.
 *      emissionFactorKey -> The factor key used (if any).
 *      emissionFactor  -> The actual factor value.
 *      co2e                    -> The calculated CO2e.
 *      sourceDescription -> Optional notes.
 */
public record EmissionRecordResponse(
        Long id,
        String facility,
        String businessUnit,
        EmissionScope scope,
        String activityType,
        LocalDate activityDate,
        BigDecimal quanitity,
        String unit,
        String emissionFactorKey,
        BigDecimal emissionFactor,
        BigDecimal co2e,
        String sourceDescription
        ) {

}
