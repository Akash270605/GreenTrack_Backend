/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * This is a custom Exception (error) class.
 * 
 * In Java, exceptions are objects that represents error conditions. When
 * something goes wrong, it "throws" an exception, which stops normal
 * execution and passes control to an exception handler.
 * 
 * This particular exception represents a "409 Conflict" error. It's thrown when
 * the user tries to create a resource that already exists (e.g registering with an
 * already-used email).
 */
package com.example.greentrack.common;


/**
 * RuntimeException: A special type of exception that does not need to be
 * declared in the method signature. It's an "unchecked exception - the program
 * continue normally if not caught.
 */
public class ConflictException extends RuntimeException{
    public ConflictException(String message){
        super(message);     // Pass the message (a human-reable description)to the parent RuntimeException
    }
}
