/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * An Entity class representing a single emission record.
 * 
 * Each row in the "emission_records" table stores one entry 
 * of emission data (activity data + calculated CO2e).
 * 
 * This is the core data entity of the application. All dashboard metrics and
 * reports are calculated from emission records.
 */
package com.example.greentrack.emissions;

import com.example.greentrack.auth.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;


/**
 *  Key Fields:                         
 *      tenantId                        -> Multi - tenant support (which organization).
 *      facility                           -> Where the emission occurred (e.g. "Plant A").
 *      businessUnit                -> Which department/unit (e.g., "Manufacturing").
 *      scope                            -> Scope 1, 2, or 3.
 *      activityType                -> What activity caused the emission (e.g. "Diesel Consumption").
 *      activityDate                 -> When the activity took place.
 *      quantity                        -> How much activity (e.g. 500 litres).
 *      unit                                 -> The unit of the quantity (e.g., "litre", "kWh").
 *      emissionFactorKey   -> Lookup key for the emission factor used.
 *      emissionFactor           -> The actual factor value used (kgCO2e per unit).
 *      co2e                              -> The calculated CO2e = quantity * emissionFactor.
 *      sourceDescription    -> Optional notes about the data source.
 *      createdBy                    -> The user who created this record (Many-to-One relationship).
 */

@Entity
@Table(name = "emission_records")
public class EmissionRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false, length = 80)
    private String tenantId;
    
    @Column(nullable = false, length = 160)
    private String facility;
    
    @Column(name = "business_unit", nullable = false, length = 160)
    private String businessUnit;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmissionScope  scope;
    
    @Column(name = "activity_type", nullable = false, length = 140)
    private String activityType;
    
    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;
    
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal  quantity;
    
    @Column(nullable = false, length = 40)
    private String unit;
    
    @Column(name = "emission_factor_key", length = 120)
    private String emissionFactorKey;
    
    @Column(name = "emission_factor", nullable = false, precision = 18, scale = 6)
    private BigDecimal emissionFactor;
    
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal co2e;
    
    @Column(name = "source_description")
    private String sourceDescription;
    
    // Many-to-One relation: Many records can be created by single user.
    // FetchType.LAZY means the User data is loaded only when accessed.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();
    
    
    // ------- Getters & Setters ------------

    public Long getId() { return id; }
    
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public EmissionScope getScope() {
        return scope;
    }

    public void setScope(EmissionScope scope) {
        this.scope = scope;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(LocalDate activityDate) {
        this.activityDate = activityDate;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getEmissionFactorKey() {
        return emissionFactorKey;
    }

    public void setEmissionFactorKey(String emissionFactorKey) {
        this.emissionFactorKey = emissionFactorKey;
    }

    public BigDecimal getEmissionFactor() {
        return emissionFactor;
    }

    public void setEmissionFactor(BigDecimal emissionFactor) {
        this.emissionFactor = emissionFactor;
    }

    public BigDecimal getCo2e() {
        return co2e;
    }

    public void setCo2e(BigDecimal co2e) {
        this.co2e = co2e;
    }

    public String getSourceDescription() {
        return sourceDescription;
    }

    public void setSourceDescription(String sourceDescription) {
        this.sourceDescription = sourceDescription;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    
}
