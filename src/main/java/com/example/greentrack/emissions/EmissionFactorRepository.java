/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * This repository provides database access for the EmissionFactor entity.
 * Spring Data JPA automatically implements the CRUD operations. We only
 * need to define custom query methods.
 */
package com.example.greentrack.emissions;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmissionFactorRepository extends JpaRepository<EmissionFactor, Long>{
    
    Optional<EmissionFactor> findByTenantIdAndFactorKeyAndActiveTrue(String tenantId, String factorKey);
    
    Optional<EmissionFactor> findByTenantIdAndFactorKeyAndScopeAndActiveTrue(
                String tenantId, String factorKey, EmissionScope scope);
    
    List<EmissionFactor> findByTenantIdAndActiveTrue(String tenantId);
}
