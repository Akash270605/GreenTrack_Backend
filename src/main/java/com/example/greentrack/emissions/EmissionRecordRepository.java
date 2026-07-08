/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *  This repository provides database access for the EmissionRecord entity.
 *  It extends JpaRepository (for basic CRUD) And JpaSpecificationExecutor
 * (for dynamic/custom queries using the Specification interface).
 */
package com.example.greentrack.emissions;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


/**
 *  Interfaces uses -
 *      - JpaRepository: Gives us save(), findById(), findAll(), delete(), etc.
 *      - JpaSpecificationExecutor: Gives us findAll(Specification) which allows
 *                                              building dynamic Where clauses at runtime.
 * 
 *  Custom method:
 *     findByTenantIdAndActivityDateBetween(tenantId, from, to): Used by
 *                          DashboardService to fetch records for a date range.
 */
public interface EmissionRecordRepository extends JpaRepository<EmissionRecord, Long>,
                                                                    JpaSpecificationExecutor<EmissionRecord>{
    
    List<EmissionRecord> findByTenantIdAndActivityDateBetween(
                    String tenantId, LocalDate from, LocalDate to);
}
