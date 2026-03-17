package com.knowvault.model;

/**
 * User - Entity representing a system user.
 * Extends BaseEntity to inherit common fields (id, createdAt, updatedAt).
 *
 * @author Sebastián González Tabares
 */
public class User extends BaseEntity {

    private String username;
    private String email;
    private String passwordHash;
    private String fullName;
    private String role;

    // ==============================
    // Implement abstract method
    // ==============================

    @Override
    public Long getId() {
        return id;
    }

    // Alias for compatibility
    public Long getUserId() {
        return id;
    }

    public void setUserId(Long userId) {
        this.id = userId;
    }

    // ==============================
    // Getters and Setters
    // ==============================

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}