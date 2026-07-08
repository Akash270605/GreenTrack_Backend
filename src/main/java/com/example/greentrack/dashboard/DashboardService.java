/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * The Service layer that compiles all dashboard data.
 * 
 * This class contains logic for:
 *      - Calculating total CO2e emissions.
 *      - Breaking down emissions by scope (Scope 1, 2, 3).
 *      - Computing monthly and yearly trends.
 *      - Calculating year-over-year change percentage.
 */
package com.example.greentrack.dashboard;

import com.example.greentrack.auth.User;
import com.example.greentrack.dashboard.dto.DashboardMetric;
import com.example.greentrack.dashboard.dto.DashboardResponse;
import com.example.greentrack.dashboard.dto.TrendPoint;
import com.example.greentrack.emissions.EmissionRecord;
import com.example.greentrack.emissions.EmissionRecordRepository;
import com.example.greentrack.emissions.EmissionScope;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Service -> Marks this as a Spring Service (business logic bean).
 */
@Service
public class DashboardService {
    
    private final EmissionRecordRepository recordRepository;
    
    // Constructor injection;
    // recordRepository: Repository for fetching emission records.
    public DashboardService(EmissionRecordRepository recordRepository){
        this.recordRepository = recordRepository;
    }
    
    /**
     * getDashboard(): Main method that compiles the full dashboard response.
     * 
     * Steps:
     *      1. Determine the target year (default: current year).
     *      2. Fetch all records for the tenant for the last 2 years.
     *      3. Apply optional filters (facility, businessUnit).
     *      4. Separate records into current year and previous year.
     *      5. Calculate KPIs, scope totals, trends and YOY change.
     *      6. Return a DashboardResponse.
     * 
     * @Transactional(readOnly = true) -> Optimises for read-only operations.
     */
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(User user, String facility, String businessUnit, Integer year){
        // Step 1: Determine the target year.
        int targetYear = year == null ? LocalDate.now().getYear() : year;
        
        // Step 2: Fetch records from Jan 1 of (targetYear - 1) to Dec 31 of targetYear.
        LocalDate from = LocalDate.of(targetYear - 1, 1, 1);
        LocalDate to = LocalDate.of(targetYear, 12, 31);
        List<EmissionRecord> records = recordRepository
                .findByTenantIdAndActivityDateBetween(user.getTenantId(), from, to)
                .stream()
                
                //Step 3: Apply optional filters
                .filter(record -> facility == null || facility.isBlank()
                        || record.getFacility().equals(facility))
                .filter(record -> businessUnit == null || businessUnit.isBlank()
                        || record.getBusinessUnit().equals(businessUnit))
                .toList();
        
        // Step 4: Separate into current year and previous year records.
        List<EmissionRecord> currentYear = records.stream()
                .filter(record -> record.getActivityDate().getYear() == targetYear)
                .toList();
        
        List<EmissionRecord> previousYear = records.stream()
                .filter(record -> record.getActivityDate().getYear() == targetYear -1)
                .toList();
        
        // Step 5a: Calculate total emissions.
        BigDecimal total = sum(currentYear);
        BigDecimal previousTotal = sum(previousYear);
        
        // Step 5b: Calculate year-over-year change percentage.
        // Formula: ((current - previous) / previous) * 100
        // If previous is 0, YoY is 0 (to avoid division by zero).
        BigDecimal yoy = previousTotal.signum() == 0
                ? BigDecimal.ZERO
                : total.subtract(previousTotal)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(previousTotal, 2, RoundingMode.HALF_UP);
        
        // Step 6: Build and return the response.
        return new DashboardResponse(
                // KPIs
                List.of(
                        new DashboardMetric("Total CO2e", total, "kgCO2e"),
                        new DashboardMetric("Emission records",
                                BigDecimal.valueOf(currentYear.size()), "records"),
                        new DashboardMetric("Average CO2e per record",
                                average(total, currentYear.size()), "kgCO2e")
                ),
                
                // Scope breakdown
                byScope(currentYear),
                
                // Monthly trend
                monthlyTrends(currentYear),
                
                // Yearly trend (uses all records across both years)
                yearlyTrends(records),
                
                // YoY change
                yoy
        );
    }
    
    
    // --------- Private Helper Method -----------
    
    /**
     * sum(): Calculates the sum of CO2e values for a list of records.
     * Return the total CO2e, rounded to 4 decimal places.
     * 
     * record: List of emission records.
     */
    private BigDecimal sum(List<EmissionRecord> records){
        return records.stream()
                .map(EmissionRecord::getCo2e)                           // Extracts CO2e from each record
                .reduce(BigDecimal.ZERO, BigDecimal::add)   // Sum them up
                .setScale(4, RoundingMode.HALF_UP);                // Round to 4 decimal places
    }
    
    /**
     * average(): Calculates the average CO2e per record.
     * Returns the average, or 0 if count is 0.
     * 
     * total: Total CO2e.
     * count: No. of records.
     */
    private BigDecimal average(BigDecimal total, int count){
        if(count == 0){
            return BigDecimal.ZERO;
        }
        return total.divide(BigDecimal.valueOf(count), 4, RoundingMode.HALF_UP);
    }
    
    /**
     * byScope(): Groups emissions by Scope (1, 2, 3) and calculate totals.
     *              Returns a map: scope name -> total CO2e.
     * 
     * records: List of emission records.
     */
    private Map<String, BigDecimal> byScope(List<EmissionRecord> records){
        
        //EnumMap is like HashMap but more effective for enum keys.
        Map<EmissionScope, BigDecimal> result = new EnumMap<>(EmissionScope.class);
        
        // Initialize all scopes to zero.
        for(EmissionScope scope : EmissionScope.values()){
            result.put(scope, BigDecimal.ZERO);
        }
        
        // Add each record's CO2e to its scope bucket.
        records.forEach(record -> result.merge(record.getScope(), record.getCo2e(), BigDecimal::add));
        
        // Convert to Map<String, BigDecimal> for the response.
        return result.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),               // Key: "SCOPE_1", etc.
                        entry -> entry.getValue().setScale(4, RoundingMode.HALF_UP),
                        (left, right) -> left,                                     // Merge function
                        LinkedHashMap::new                              // Preserve insertion order
                ));
    }
    
    /**
     * monthlyTrends(): Groups emissions by month and calculates 
     * totals for each month.
     * Returns a sorted list of TrendPoints (one per month).
     */
    private List<TrendPoint> monthlyTrends(List<EmissionRecord> records){
        
        // Group by YearMonth (e.g., 2026-01) and sum CO2e values.
        Map<YearMonth, BigDecimal> grouped = records.stream()
                .collect(Collectors.groupingBy(
                        record -> YearMonth.from(record.getActivityDate()),
                        Collectors.mapping(EmissionRecord:: getCo2e, 
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
        
        // Sort by month and convert to TrendPoints.
        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new TrendPoint(
                        entry.getKey().toString(),                              // "2026-01"
                        entry.getValue().setScale(4, RoundingMode.HALF_UP)
                ))
                .toList();
    }
    
    /**
     * yearlyTrends(): Groups emissions by year and calculates totals for
     *                  each year.
     * Returns a sorted list of TrendPoints (one per year).
     * 
     * records: List of emission records (across multiple years).
     */
    private List<TrendPoint> yearlyTrends(List<EmissionRecord> records){
        return records.stream()
                .collect(Collectors.groupingBy(
                        record -> Integer.toString(record.getActivityDate().getYear()),
                        Collectors.mapping(EmissionRecord::getCo2e,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ))
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))  // Sort by year string
                .map(entry -> new TrendPoint(
                        entry.getKey(),
                        entry.getValue().setScale(4, RoundingMode.HALF_UP)
                ))
                .toList();
    }
}