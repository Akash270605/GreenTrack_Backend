/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * This file defines an ENUM (a special type that represents fixed set of constants) for
 * different user roles
 */
package com.example.greentrack.auth;


/**
 * Role Hierarchy:
 *      ADMIN                   -> Full access to everything in the system.
 *      ESG_MANAGER  -> Can manage ESG (Environment, Social, Governance) data
 *                                      and emission records.
 *      ANALYST               -> Can create and edit emission records but may have fewer
 *                                      administrative privileges.
 *      VIEWER                 -> Can only view / read data, cannot create or modify.
 */
public enum Role {
    ADMIN,
    ESG_MANAGER,
    ANALYST,
    VIEWER
}
