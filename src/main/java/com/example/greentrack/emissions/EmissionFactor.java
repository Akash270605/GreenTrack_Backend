/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * An Entity class representing an emission factor.
 * 
 * Emission Factor: It is a coefficient that converts an activity (e.g., burning
 *           1 litre of diesel) into CO2e emissions.
 */
package com.example.greentrack.emissions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;


/**
 * Fields:
 *      id                      -> Auto-generated primary key.
 *      tenantId          -> Which tenant this factor belongs to.
 *                                  "default" tenant provides system-wide factors.
 *      factorKey       -> A unique key for looking up the factor (e.g., "diesel_litre",
 *                                  "electricity_kwh").
 *      name              -> A human-readable name (e.g. "Diesel combustion per litre").
 *      scope              -> Which emission scope this factor applies to.
 *      unit                  -> The unit of the activity (e.g. "litre", "kwh").
 *      co2eFactor    -> The conversion factor (kgCO2e per unit).
 *      effectiveFrom -> The date from which this factor is effective.
 *      active               -> Whether this factor is currently in use.
 *      
 */

@Entity
@Table(name = "emission_factors")
public class EmissionFactor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false, length = 80)
    private String tenantId;
    
    @Column(name = "factor_key", nullable = false, length = 120)
    private String factorKey;
    
    @Column(nullable = false, length = 180)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmissionScope scope;
    
    @Column(nullable = false, length = 40)
    private String unit;
    
    @Column(name = "co2e_factor", nullable = false, precision = 18, scale = 6)
    private BigDecimal co2eFactor;
    
    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;
    
    @Column(nullable = false)
    private boolean active = true;
    
    
    
    // --------- Getters and Setters -------------
    public Long getId() { return id; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    
    public String getFactorKey() { return factorKey; }
    public void setFactorKey(String factorKey) { this.factorKey = factorKey; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public EmissionScope getScope() { return scope; }
    public void setScope(EmissionScope scope) { this.scope = scope; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public BigDecimal getCo2eFactor() { return co2eFactor; }
    public void setCo2eFactor(BigDecimal co2eFactor) { this.co2eFactor = co2eFactor; }
    
    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    
    public boolean isActive(){ return active; }
}

