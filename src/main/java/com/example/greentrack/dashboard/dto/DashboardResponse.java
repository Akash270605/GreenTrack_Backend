/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

/**
 * A DTO representing the full dashboard response. This is the main data object
 * sent to the frontend when it requests the dashboard.
 * 
 * It contains:
 *      - KPI metric (total emissions, record count, average).
 *      - Emission broken down by scope (Scope 1, 2, 3).
 *      - Monthly and yearly trend data.
 *      - Year-over-year change percentage.
 */
package com.example.greentrack.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * DashboardResponse: The JSON structure returned by the GET /api/v2/dashboard endpoint
 * 
 * Fields:
 *      kpis                            -> A list of key metric (Total CO2e, no. of records, 
 *                                  average CO2e per record).
 *      emissionsByScope -> A map where keys are scope names ("SCOPE_1",
 *                                  "SCOPE_2", "SCOPE_3) and values are total CO2e for that scope.
 *      monthlyTrends            -> A list of monthly totals for charting.
 *      yearlyTrends                -> A list of yearly totals for charting.
 *      yearOverYearChangePercent -> Percentage change compared to previous
 *                                              year (negative = reduction = good!).
 */
public record DashboardResponse(
        List<DashboardMetric> kpis,
        Map<String, BigDecimal> emissionsByScope,
        List<TrendPoint> monthlyTrends,
        List<TrendPoint> yearlyTrends,
        BigDecimal yearOverYearChangePercent
        ) {

}
