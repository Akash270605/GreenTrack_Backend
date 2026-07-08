/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

/**
 * A DTO representing single data point in a trend chart (e.g., a bar chart
 * or line graph).
 * 
 * Each trend point has:
 *      - period: The time period (e.g. "2026-01" for Jan 2026).
 *      - co2e:     The total CO2e emissions for that period.
 */
package com.example.greentrack.dashboard.dto;

import java.math.BigDecimal;


// TrendPoint: Used to construct chart data for the frontend. Each point 
// represents the total emissions in a specific time bucket.
public record TrendPoint(
        String period,
        BigDecimal co2e
        ) {

}
