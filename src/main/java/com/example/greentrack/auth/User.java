/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * This is an Entity class representing a user in the database.
 * 
 * An "Entity" is a Java class that is mapped to a database table.
 * Each instance of this class corresponds to one row in the "users" table. 
 * The fields represent the columns in the table.
 */
package com.example.greentrack.auth;

// Jakarta Persistance (JPA) annotations map Java fields to DB columns
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

// Spring Security interfaces for authentication.
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
        

/**
 *  Annotations:
 *      @Entity                                     -> Marks this class as a JPA entity (maps to a DB table)
 *      @Table(name="users)         -> Specifies the table name in the DB.
 *      @Id                                             -> Marks the primary key field
 *      @GeneratedValue                -> Tells JPA to auto-generate the ID (AUTO_INCREMENT).
 *      @Column(...)                          -> Maps a Java field to a DB column with options like: nullable,
 *                                                                  length, unique, etc.
 *      @Enumerated                        -> Tells JPA to store the enum as String in the DB
 */

@Entity
@Table(name ="users")
public class User implements UserDetails{
    
    // Each user gets a unique, auto-incrementing Long ID.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    //Tenant ID : Identifies which organization this user belongs to.
    // In a multi-tenant system, each company/customer is a "tenant" and sees 
    // only their own data.
    @Column(name="tenant_id", nullable = false, length = 80)
    private String tenantId;
    
    // Full Name: Display name for the user
    @Column(name = "full_name", nullable = false, length = 160)
    private String fullName;
    
    // Email: used as the login username, Must be unique across all users.
    @Column(nullable = false, unique = true, length = 180)
    private String email;
    
    // Password Hash: The BCrypted - encrypted hash of the user's password
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    // Role: The user's role (ADMIN, ESG_MANAGER, ANALYST, VIEWER)
    // Stored as a string in the DB
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Role role;
    
    // Enabled: Whether the user account is active.
    // Disabled users cannot log in.
    @Column(nullable = false)
    private boolean enabled = true;
    
    // Created at: Timestamp when this user was created
    // updatable = false  -> once set, it cannot be changed
    @Column(name = "created_at", nullable = false, updatable =false)
    private Instant createdAt = Instant.now();
    
    // Updated At: Timestamp when this user was last updated.
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();
    
    
    //-------- Getters & Setters -------------
    public Long getId() { return id; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
    
   // ------------ User Details Implementations --------
   // Sppring Security calls this method during authentication
    
    /**
     * getAuthorities(): Returns the list of roles / privileges this user has.
     * Spring Security uses this to check @PreAuthorize annotations.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    /**
     * getPassword(): Returns the stored password hash.
     * Spring Security will compare the login password (after hashing) against
     * this value using the PasswordEncoder.
     */
    @Override
    public String getPassword() { return passwordHash; }
    
    /**
     * getUserName(): Returns the "username" - we use email.
     */
    @Override
    public String getUsername() { return email; }
    
    /**
     * isEnabled(): Tells Spring Security whether this account is active.
     * Disabled users cannot authenticate.
     */
    @Override
    public boolean isEnabled() { return enabled; }
}
