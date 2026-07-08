/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */

/**
 * This file defines an ENUM for the three emission scopes according to the 
 * Greenhouse Gas (GHG) Protocol.
 * 
 *  Three types:
 *      SCOPE_1: Direct emissions from the sources Owned or controlled
 *                              by the company (e.g. fuel combustion in company vehicles,
 *                              on-site generators).
 * 
 *      SCOPE_2: Indirect emissions from Purchased electricity, steam, heating
 *                              and cooling.
 *      
 *      SCOPE_3: All other indirect emissions in the value chain (e.g. business travel,
 *                              purchased goods, waster disposal, employee commuting).
 */
package com.example.greentrack.emissions;


/**
 * Usage: Used in EmissionRecord to indicate which scope an emission belongs to, and 
 *              in EmissionFactor to indicate which scope a particular emission 
 *              factor applies to.
 */
public enum EmissionScope {
    SCOPE_1,
    SCOPE_2,
    SCOPE_3
}
