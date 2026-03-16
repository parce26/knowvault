package com.knowvault.repository;

import com.knowvault.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    Optional<User> findById(int userId);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    int save(User user);          // retorna id creado
    boolean update(User user);    // true si actualizó 1 fila
    boolean deleteById(int userId);
}