package com.knowvault.repository;

import com.knowvault.model.User;

/**
 * UserRepository - Interface defining the contract for user data access.
 * JdbcUserRepository provides the concrete JDBC implementation.
 *
 * @author Sebastián González Tabares
 */
public interface UserRepository {

    User findById(Long id);

    User findByEmail(String email);

    User findByUsername(String username);

    void save(User user);

    void update(User user);

    void delete(Long id);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}