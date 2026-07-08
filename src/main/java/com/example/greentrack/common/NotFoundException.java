/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * This is a custom Exception class.
 * 
 * It represents a "404 Not Found" error. It's thrown when someone tries to
 * access a resource that does not exist in the database.
 */
package com.example.greentrack.common;

/**
 *
 * @author Leveno
 */
public class NotFoundException extends RuntimeException{
    
    // message: Description of what was not found.
    public NotFoundException(String message){
        super(message);
    }
}
