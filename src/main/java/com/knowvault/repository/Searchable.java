package com.knowvault.repository;

import java.util.List;

/**
 * Searchable - Interface for repositories that support keyword search.
 * Implemented by repositories that need full-text or keyword search capability.
 *
 * @param <T> the entity type returned by the search
 *
 * @author Sebastián González Tabares
 */
public interface Searchable<T> {

    /**
     * Searches entities matching the given keyword.
     *
     * @param keyword the search term
     * @return list of matching entities
     */
    List<T> search(String keyword);
}