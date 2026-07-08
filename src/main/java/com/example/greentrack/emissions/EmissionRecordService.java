/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 * This Service handles CRUD operations for emission records.
 * Also converts between entities and DTOs.
 * 
 * CRUD stands for: 
 *      C - Create (add new records).
 *      R - Read (list/search records).
 *      U - Update (modify existing records).
 *      D - Delete (remove records).
 * 
 * It also builds dynamic database queries using the specification pattern
 * (for filtering by multiple criteria).
 */
package com.example.greentrack.emissions;

import com.example.greentrack.auth.User;
import com.example.greentrack.common.NotFoundException;
import com.example.greentrack.emissions.dto.EmissionRecordRequest;
import com.example.greentrack.emissions.dto.EmissionRecordResponse;

import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class EmissionRecordService {
    
    private final EmissionRecordRepository recordRepository;
    private final EmissionCalculatorService calculatorService;
    
    public EmissionRecordService(EmissionRecordRepository recordRepository,
                EmissionCalculatorService calculatorService){
        this.recordRepository = recordRepository;
        this.calculatorService = calculatorService;
    }
    
    /**
     * list(): Searches for emission records with optional filters.
     * Returns the list of EmissionRecordResponse DTOs.
     * 
     * Transactional(readOnly = true) -> Read-only optimization.
     */
    @Transactional(readOnly = true)
    public List<EmissionRecordResponse> list (User user, String facility,
                                                String businessUnit, EmissionScope scope,
                                                LocalDate from, LocalDate to){
        
        // Use the dynamic specification (filters) to build the Where clause.
        return recordRepository
                .findAll(filters(user.getTenantId(), facility, businessUnit, scope, from, to))
                .stream()
                .map(this::toResponse)          // Convert each entity to DTO
                .toList();
    }
    
    /**
     * create(): Creates a new emission record
     * 
     *      user       -> Authenticated user (owner of the record).
     *      request -> The emission data from the client.
     * 
     *  @Transactional - > Runs in a database transaction.
     */
    public EmissionRecordResponse create(User user, EmissionRecordRequest request){
        
        // Convert the request -> entity, save to DB, convert back to DTO.
        return toResponse(recordRepository.save(toEntity(user, request, new EmissionRecord())));
    }
    
    /**
     *  update(): Updates an existing emission record
     * 
     * Steps:
     *      1. Find the record by ID
     *      2. Update its fields with the new request data.
     *      3. Save and return the updated record.
     */
    @Transactional
    public EmissionRecordResponse update(User user, Long id, EmissionRecordRequest request){
        EmissionRecord record = requireTenantRecord(user, id);
        return toResponse(recordRepository.save(toEntity(user, request, record)));
    }
    
    
      // delete(): Deletes an emission record.
     @Transactional
     public void delete(User user, Long id){
         EmissionRecord record = requireTenantRecord(user, id);
         recordRepository.delete(record);
     }
    
     /**
      * requireTenantRecord(): Finds a record by ID and verifies it belongs to the user's tenant.
      * This ensures users can only access their own organization's data.
      * 
      * Returns the EmissionRecord if found and belongs to the same tenant.
      * Throws NotFoundException if not found or tenant mismatch.
      */
     @Transactional(readOnly = true)
     public EmissionRecord requireTenantRecord(User user, Long id){
         EmissionRecord record = recordRepository.findById(id)
                 .orElseThrow(() -> new NotFoundException("Emission record not found"));
         
         // Tenant check: if the record belongs to a different tenant, treat as "not found"
        //                                (don't reveal that it exists).
        if(!record.getTenantId().equals(user.getTenantId())){
            throw new NotFoundException("Emission record not found");
        }
        return record;
     }
     
     
     /**
      * toEntity(): Converts a request DTO + user into an EmissionRecord entity.
      * This also calculates the CO2e using the EmissionCalculatorService.
      * 
      * request -> The request data.
      * record  -> An existing (or new ) entity to populate.
      * 
      * Returns the populated EmissionRecord.
      */
     public EmissionRecord toEntity(User user, EmissionRecordRequest request, EmissionRecord record){
         
         // Resolves the emission factor (from DB or supplied directly).
         BigDecimal factor = calculatorService.resolveFactor(
                 user.getTenantId(),
                 request.scope(),
                 request.emissionFactorKey(),
                 request.emissionFactor()
         );
                 
         // Map fields from request to entity.
         record.setTenantId(user.getTenantId());
         record.setFacility(request.facility());
         record.setBusinessUnit(request.businessUnit());
         record.setScope(request.scope());
         record.setActivityType(request.activityType());
         record.setActivityDate(request.activityDate());
         record.setQuantity(request.quantity());
         record.setUnit(request.unit());
         record.setEmissionFactorKey(request.emissionFactorKey());
         record.setEmissionFactor(factor);
         
         // Calculate CO2e = quantity * factor.
         record.setCo2e(calculatorService.calculateCo2e(request.quantity(), factor));
         record.setSourceDescription(request.sourceDescription());
         record.setCreatedBy(user);
         return record;
     }         
     
     /**
      * toResponse(): Converts an EmissionRecord entity into a response DTO
      *             (to send back to the client as JSON).
      * 
      *  record  -> The entity from the database.
      */
     public EmissionRecordResponse toResponse(EmissionRecord record){
         return new EmissionRecordResponse(
                 record.getId(),
                 record.getFacility(),
                 record.getBusinessUnit(),
                 record.getScope(),
                 record.getActivityType(),
                 record.getActivityDate(),
                 record.getQuantity(),
                 record.getUnit(),
                 record.getEmissionFactorKey(),
                 record.getEmissionFactor(),
                 record.getCo2e(),
                 record.getSourceDescription()
         );
     }
     
     /**
      * filters(): Builds as dynamic Where clause (Specification) based on the provided
      *                 filter parameters. Only non-null/non-blank filters are included in the query.
      * 
      * Returns a Specification object that Spring Data JPA uses to build the
      * final SQL query.
      */
     private Specification<EmissionRecord> filters(String tenantId, String facility,
                                String businessUnit, EmissionScope scope,
                                LocalDate from, LocalDate to){
         
         return (root, query, cb) -> {
             //List of predicates (conditions) to combine with AND
             List<Predicate> predicates = new ArrayList<>();
             
             // Always filter by tenant (data isolation).
             predicates.add(cb.equal(root.get("tenantId"), tenantId));
             
             // Optional filters: only add if provided.
             if(facility != null && !facility.isBlank()){
                 predicates.add(cb.equal(root.get("facility"), facility));
             }
             
             if(businessUnit != null && !businessUnit.isBlank()){
                 predicates.add(cb.equal(root.get("businessUnit"), businessUnit));
             }
             
             if(scope != null){
                 predicates.add(cb.equal(root.get("scope"), scope));
             }
             
             if(from != null){
                 predicates.add(cb.greaterThanOrEqualTo(root.get("acitivityDate"), from));
             }
             
             if(to != null){
                 predicates.add(cb.lessThanOrEqualTo(root.get("activityDate"), to));
             }
             
             // Order by activityDate descending, then by id descending
             // (Most recent records first).
             query.orderBy(cb.desc(root.get("activityDate")), cb.desc(root.get("id")));
             
             // Combines all predicates with AND
             return cb.and(predicates.toArray(Predicate[]::new));             
         };
     }
}
