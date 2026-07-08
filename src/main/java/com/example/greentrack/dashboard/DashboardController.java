/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * REST Controller for dashboard-related endpoints.
 * 
 * Provides a single endpoint that returns all the data needed to render the 
 * frontend dashboard (KPIs, scope breakdown, trends, etc.)
 */
package com.example.greentrack.dashboard;

import com.example.greentrack.auth.User;
import com.example.greentrack.dashboard.dto.DashboardResponse;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * EndPoint:
 *      GET /api/v2/dashboard -> Returns aggregated dashboard data.
 *                                                          Supports optional filters: facility, businessUnit,
 *                                                      and year.
 */
@RestController
@RequestMapping("/api/v2/dashboard")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    // Constructor injection.
    public DashboardController(DashboardService dashboardService){
        this.dashboardService = dashboardService;
    }
    
    /**
     * GET /api/v2/dashboard: Returns dashboard data for the currently 
     *                              authenticated user's tenant.
     * 
     * Filter Parameters:
     *      @RequestParam -> Spring binds query parameters from the URL.
     * 
     *      facility             -> Filter by facility name. (e.g., "Plant A")
     *      businessUnit -> Filter by business unit (e.g., "Manufacturing").
     *      year                 -> Filter by year (e.g., "2026"). Default to current year.
     * 
     * authentication:  Spring Security's Authentication (injected automatically).
     * 
     * Returns DashboardResponse containing KIPs, scope totals, trends, etc.
     */
    @GetMapping
    public DashboardResponse getDashboard(
            Authentication authentication,
            @RequestParam(required = false) String facility,
            @RequestParam(required = false) String businessUnit,
            @RequestParam(required = false) Integer year
    ){
        
        // Delegate to the service layer.
        return dashboardService.getDashboard(
                (User) authentication.getPrincipal(),
                facility,
                businessUnit,
                year
        );
    }
}
