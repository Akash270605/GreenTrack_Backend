/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  Leveno
 * Created: 29 Jun 2026
 */

-- This is a Flyway Database Migration file.

-- Flyway is a tool that manages databases schema versions.
-- Migration files are SQL scripts that are executed in order based on their version number.

-- It creates the initial database schmea: tables for users, emission factors, and emission records,
--  plus some default emission factor values.


# Table users: Stores user accounts for authentication.
CREATE TABLE users (
    id              BIGINT       NOT NULL AUTO_INCREMENT,  -- Primary key, auto-generated
    tenant_id       VARCHAR(80)  NOT NULL,                         -- Multi-tenant organisation ID
    full_name       VARCHAR(160) NOT NULL,                       -- User's display name
    email           VARCHAR(180) NOT NULL,                           -- Login email (unique)
    password_hash   VARCHAR(255) NOT NULL,                -- BCrypt-hashed password
    role            VARCHAR(40)  NOT NULL,                              -- ADMIN, ESG_MANAGER, ANALYST, VIEWER
    enabled         BOOLEAN      NOT NULL DEFAULT TRUE,    -- Is the account active?
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email),           -- Ensure email uniqueness
    KEY idx_users_tenant (tenant_id)                   -- Index for tenant-based queries
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


# Table emission-factors: Stores conversion factors for calculating CO2e from the
# activity data.
CREATE TABLE emission_factors (
    id              BIGINT        NOT NULL AUTO_INCREMENT,  -- Primary key
    tenant_id       VARCHAR(80)   NOT NULL,                         -- Tenant (or "default")
    factor_key      VARCHAR(120)  NOT NULL,                       -- Lookup key (e.g., "diesel_litre")
    name            VARCHAR(180)  NOT NULL,                          -- Human-readable name
    scope           VARCHAR(20)   NOT NULL,                            -- SCOPE_1, SCOPE_2, or SCOPE_3
    unit            VARCHAR(40)   NOT NULL,                               -- Unit of input (e.g., "litre")
    co2e_factor     DECIMAL(18,6) NOT NULL,                       -- kgCO2e per unit
    effective_from  DATE          NOT NULL,                              -- When this factor becomes valid
    active          BOOLEAN       NOT NULL DEFAULT TRUE,   -- Is this factor currently active?
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_factor_tenant_key (tenant_id, factor_key),  -- One factor key per tenant
    KEY idx_factors_tenant_scope (tenant_id, scope)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


# Table emission_records: Stores individual emission entries 
# (activity data + calculated CO2e).
CREATE TABLE emission_records (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,  -- Primary key
    tenant_id           VARCHAR(80)   NOT NULL,                        -- Tenant identifier
    facility            VARCHAR(160)  NOT NULL,                           -- Facility name (e.g., "Plant A")
    business_unit       VARCHAR(160)  NOT NULL,                   -- Department/unit name
    scope               VARCHAR(20)   NOT NULL,                           -- SCOPE_1, SCOPE_2, SCOPE_3
    activity_type       VARCHAR(140)  NOT NULL,                    -- Type of activity (e.g., "Diesel Consumption")
    activity_date       DATE          NOT NULL,                               -- When the activity occurred
    quantity            DECIMAL(18,4) NOT NULL,                         -- Amount of activity
    unit                VARCHAR(40)   NOT NULL,                              -- Unit of quantity
    emission_factor_key VARCHAR(120),                                 -- Key used to look up the factor
    emission_factor     DECIMAL(18,6) NOT NULL,                 -- Actual factor value used
    co2e                DECIMAL(18,4) NOT NULL,                           -- Calculated CO2e = quantity × factor
    source_description  VARCHAR(255),                                  -- Optional notes
    created_by          BIGINT,                                                         -- Foreign key → users(id)
    created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_records_tenant_date (tenant_id, activity_date),      -- For date-range queries
    KEY idx_records_tenant_scope (tenant_id, scope),                -- For scope filtering
    KEY idx_records_tenant_facility (tenant_id, facility),             -- For facility filtering
    KEY idx_records_tenant_business_unit (tenant_id, business_unit), -- For business unit filtering
    CONSTRAINT fk_records_created_by FOREIGN KEY (created_by) REFERENCES users(id) -- Link to user
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- Default Emission Factors:
-- They server as fallback values when a tenant doesn't have custom factors defined.
INSERT INTO emission_factors (tenant_id, factor_key, name, scope, unit, co2e_factor, effective_from, active)
VALUES 
-- Scope 1: Diesel combustion (2.68 kgCO2e per litre)
('default', 'diesel_litre', 'Diesel combustion per litre', 'SCOPE_1', 'litre', 2.680000, '2024-01-01', TRUE),

-- Scope 1: Petrol combustion (2.31 kgCO2e per litre)
('default', 'petrol_litre', 'Petrol combustion per litre', 'SCOPE_1', 'litre', 2.310000, '2024-01-01', TRUE),

-- Scope 2: Grid electricity (0.716 kgCO2e per kWh)
('default', 'electricity_kwh', 'Grid electricity per kWh', 'SCOPE_2', 'kWh', 0.716000, '2024-01-01', TRUE),

-- Scope 3: Business travel (0.18 kgCO2e per passenger km)
('default', 'business_travel_km', 'Business travel per passenger km', 'SCOPE_3', 'km', 0.180000, '2024-01-01', TRUE);
