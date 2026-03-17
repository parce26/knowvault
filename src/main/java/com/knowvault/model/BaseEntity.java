package com.knowvault.model;

import java.time.LocalDateTime;

/**
 * BaseEntity - Abstract base class for all domain entities.
 * Provides common fields shared across all models.
 * Demonstrates inheritance in the KnowVault system.
 *
 * @author Sebastián González Tabares
 */
public abstract class BaseEntity {

    protected Long id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    // ==============================
    // Abstract methods
    // ==============================

    /**
     * Returns the unique identifier of the entity.
     */
    public abstract Long getId();

    /**
     * Checks equality based on ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    // ==============================
    // Getters and Setters
    // ==============================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}