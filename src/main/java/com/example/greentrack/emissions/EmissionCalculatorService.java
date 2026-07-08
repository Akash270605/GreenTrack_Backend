/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * This Service calculates CO2e emissions from activity data.
 * 
 * The formula is:  
 *      CO2e = Activity quantity * Emission Factor
 * 
 * The service also handles resolving the emission factor:
 *      - If the user provides a custom factor, use that.
 *      - Otherwise, look up the factor from the database by key.
 *      - If not found for the user's tenant, fall back to "default" tenant.
 */
package com.example.greentrack.emissions;

import com.example.greentrack.emissions.dto.CalculateEmissionRequest;
import com.example.greentrack.emissions.dto.CalculateEmissionResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;


@Service
public class EmissionCalculatorService {
    
    private final EmissionFactorRepository factorRepository;
    
    public EmissionCalculatorService(EmissionFactorRepository factorRepository){
        this.factorRepository = factorRepository;
    }
    
    /**
     * calculate(): Performs the full emission calculation for a request.
     * 
     * Steps:
     *      1. Resolve the emission factor.
     *      2. Calculate CO2e = quantity * factor;
     *      3. Return the response;
     * 
     * tenantId: The tenant to look up factors for.
     * request:   The calculation request (scope, quantity, unit, factor info).
     */
    public CalculateEmissionResponse calculate(String tenantId, CalculateEmissionRequest request){
        BigDecimal factor = resolveFactor(tenantId, request.scope(),
                request.emissionFactorKey(), request.emissionFactor());
        BigDecimal co2e = calculateCo2e(request.quantity(), factor);
        
        return new CalculateEmissionResponse(request.scope(), request.quantity(),
                request.unit(), factor, co2e);
    }
    
    /**
     * resolveFactor(): Finds the appropriate emission factor for calculation.
     * 
     * Logic:
     *      1. If the user provided a direct emissionFactor value -> use it.
     *      2. If not, look up by factorKey in the database.
     *      3. If not found for the user's tenant, fall back to "default" tenant.
     *      4. If still not found, throw an error (IllegalArgumentException).
     * 
     * Returns the resolved emission factor.
     */
    public BigDecimal resolveFactor(String tenantId, EmissionScope scope,
            String factorKey, BigDecimal suppliedFactor){
        
        // Priority 1: If user directly supplied a factor value, use it.
        if(suppliedFactor != null){
            return suppliedFactor;
        }
        
        // Priority 2: If no factor key provided, we can't look anything up.
        if(factorKey == null || factorKey.isBlank()){
            throw new IllegalArgumentException(
                    "Either emissionFactor or emissionFactorKey is required");
        }
        
        // Priority 3: Try user's tenant first, then fall back to "default"
        return factorRepository
                .findByTenantIdAndFactorKeyAndScopeAndActiveTrue(tenantId, factorKey, scope)
                .or(() -> factorRepository.findByTenantIdAndFactorKeyAndScopeAndActiveTrue(
                            "default", factorKey, scope))
                .map(EmissionFactor::getCo2eFactor)
                .orElseThrow(() -> new IllegalArgumentException(
                            "No active emission factor found for key " + factorKey));
    }
    
    /**
     * calculateCo2e(): Simple multiplication: 
     *          quantity * emission factor.
     * Rounds results to 4 decimal places.
     * 
     * Returns CO2e rounded to 4 decimal places.
     */
    public BigDecimal calculateCo2e(BigDecimal quantity, BigDecimal emissionFactor){
        return quantity.multiply(emissionFactor)
                .setScale(4, RoundingMode.HALF_UP);
    }
}
