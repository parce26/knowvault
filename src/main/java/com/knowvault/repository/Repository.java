package com.knowvault.repository;

import java.util.ArrayList;

/**
 * Repository - Generic base interface for all data access objects.
 * Defines the standard CRUD contract for all repositories.
 * Applies the Dependency Inversion Principle (SOLID).
 *
 * @param <T>  the entity type
 * @param <ID> the type of the entity's identifier
 *
 * @author Sebastián González Tabares
 */
public interface Repository<T, ID> {

    /**
     * Retrieves all entities.
     */
    ArrayList<T> findAll();

    /**
     * Finds a single entity by its ID.
     */
    Object findById(ID id);

    /**
     * Persists a new entity.
     */
    void save(T entity);

    /**
     * Updates an existing entity.
     */
    void update(T entity);

    /**
     * Deletes an entity by its ID.
     */
    boolean deleteById(ID id);
}
