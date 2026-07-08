/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * This is a Data Access Layer interface for the User entity.
 * 
 * "Repository" is responsible for talking to the databases. By extending
 * JpaRepository, we automatically get basic CRUD operations (save, findById, findAll, delete, etc.)
 * without writing any SQL code.
 * 
 * JpaRepository<User, Long> :
 *      - User -> The entity type this repository manages.
 *      - Long -> The data type of the entity's primary key (id).
 */
package com.example.greentrack.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * UserRepository: Provides database access for user records stored
 * in the "users" table
 * 
 * Spring Data JPA automatically implements methods based on their names.
 * Just declare the method signature and Spring writes the SQL query
 * 
 * findByEmail(String email): Returns the user if found, or empty
 * existsByEmail(String email): Returns if a user with that email exists, 
 *                                  false otherwise.
 */
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
