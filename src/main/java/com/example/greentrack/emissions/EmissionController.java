/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * REST Controller for emission record management.
 * Provides endpoints for CURD operations on emission records, plus file 
 * upload and calculation endpoints.
 */
package com.example.greentrack.emissions;

import com.example.greentrack.auth.User;
import com.example.greentrack.emissions.dto.CalculateEmissionRequest;
import com.example.greentrack.emissions.dto.CalculateEmissionResponse;
import com.example.greentrack.emissions.dto.EmissionRecordRequest;
import com.example.greentrack.emissions.dto.EmissionRecordResponse;
import com.example.greentrack.emissions.dto.UploadResponse;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
   
/**
 *Endpoints:
 *      GET         /api/v2/emissions                       -> List records (with filters).
 *      POST       /api/v2/emissions                       -> Create a new record.
 *      PUT         /api/v2/emissions/{id}               -> Update an existing record.
 *      DELETE /api/v2/emissions/{id}               -> Delete a record.
 *      POST      /api/v2/emissions/upload        -> Upload CSV/Excel file.
 *      POST     /api/v2/emissions/calculate      -> Calculate emissions without saving.
 * 
 * Role Requirements:
 *      - Create/Update:    ADMIN, ESG_MANAGER, or ANALYST.
 *      - Delete:   Only ADMIN or ESG_MANAGER
 *      - List/Calculate: Any authenticated user.
 */

@RestController
@RequestMapping("/api/v2/emissions")
public class EmissionController {
    
    private final EmissionRecordService recordService;
    private final EmissionImportService importService;
    private final EmissionCalculatorService calculatorService;
    
    public EmissionController(
            EmissionRecordService recordService,
            EmissionImportService importService,
            EmissionCalculatorService calculatorService
    ){
        this.recordService = recordService;
        this.importService = importService;
        this.calculatorService = calculatorService;
    }
    
    /**
     * GET /api/v2/emissions: Lists emission records with optional filters.
     * All authenticated users can access this.
     */
    @GetMapping
    public List<EmissionRecordResponse> list(
            Authentication authentication,
            @RequestParam(required = false) String facility,
            @RequestParam(required = false) String businessUnit,
            @RequestParam(required = false) EmissionScope scope,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ){
        return recordService.list((User) authentication.getPrincipal(),
                facility, businessUnit, scope, from, to);
    }
    
    /**
     * POST /api/v2/emissions: Create a new emission  record.
     * 
     * @PreAuthorize -> Only users with ADMIN, ESG_MANAGER, or ANALYST role.
     * @ResponseStatus(HttpStatus.CREATED) -> Returns 201 Created.
     * 
     * request: The emission record data.
     * 
     * Returns the created record.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'ESG_MANAGER', 'ANALYST')")
    public EmissionRecordResponse create(
            Authentication authentication,
            @Valid @RequestBody EmissionRecordRequest request
    ){
        return recordService.create((User) authentication.getPrincipal(), request);
    }
    
    /**
     * PUT /api/v2/emissions/{id}: Updates an existing emission record.
     * 
     * id:              The ID of the record to update.
     * request:    The updated data.  
     * 
     * Returns the updated record.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESG_MANAGER', 'ANALYST')")
    public EmissionRecordResponse update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody EmissionRecordRequest request
    ){
        return recordService.update((User) authentication.getPrincipal(), id, request);
    }
    
    /**
     * DELTE /api/v2/emissions/{id}: Deletes an emission record.
     * 
     * Only ADMIN or ESG_MANAGER can delete.
     * Returns 204 No Content on success
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'ESG_MANAGER')")
    public void delete(Authentication authentication, @PathVariable Long id){
        recordService.delete((User) authentication.getPrincipal(), id);
    }
    
    /**
     * POST /api/v2/emissions/upload: Uploads a CSV or Excel file containing
     *          emission records. The file is parsed, and each row is validated and saved.
     * 
     * Returns UploadResponse with counts of imported/rejected rows.
     */    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'ESG_MANAGER', 'ANALYST')")
    public UploadResponse upload(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ){
        return importService.importFile((User) authentication.getPrincipal(), file);
    }
    
    /**
     * POST /api/v2/emissions/calculate: Calculate CO2e emissions from 
     *          provided data without saving a record. Useful for "what-if" scenario
     *          or preview calculations.
     * 
     * request: The calculation request (scope, quantity, factor).
     * 
     * Returns the calculated emission result.
     */
    @PostMapping("/calculate")
    public CalculateEmissionResponse calculate(
            Authentication authentication,
            @Valid @RequestBody CalculateEmissionRequest request
    ){
        return calculatorService.calculate(
                ((User) authentication.getPrincipal()).getTenantId(), request
        );
    }
}
