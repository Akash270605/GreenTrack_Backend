/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * A DTO (Data Transfer Object) representing a single KPI (Key Performance
 * Indicator) on the dashboard.
 */
package com.example.greentrack.dashboard.dto;

import java.math.BigDecimal;

/**
 * Fields:
 *      label -> A display name for the metric (e.g. "Total CO2e").
 *      value -> The numeric value (e.g., 15000.5000).
 *      unit    -> The unit of measurement (e.g., "kgCO2e", "records")
 */
public record DashboardMetric(
        String label,
        BigDecimal value,
        String unit
        ) {
    
}
