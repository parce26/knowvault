package com.knowvault.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.knowvault.exception.DuplicateResourceException;
import com.knowvault.exception.ResourceNotFoundException;
import com.knowvault.model.User;
import com.knowvault.model.dto.UserCreateForm;
import com.knowvault.model.dto.UserUpdateForm;
import com.knowvault.repository.JdbcUserRepository;
import com.knowvault.repository.UserRepository;

/**
 * UserService - Business logic layer for user management.
 * Depends on the UserRepository interface, not the concrete implementation.
 * Applies the Dependency Inversion Principle (SOLID).
 *
 * @author Sebastián González Tabares
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==============================
    // Get all users
    // ==============================

    public List<User> getAllUsers() {
        return ((JdbcUserRepository) userRepository).findAll();
    }

    // ==============================
    // Get user by ID
    // ==============================

    public User getUserById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return user;
    }

    // ==============================
    // Create user
    // ==============================

    public void createUser(UserCreateForm form) {
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (userRepository.existsByUsername(form.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        String encodedPassword = passwordEncoder.encode(form.getPassword());

        User user = new User();
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setPasswordHash(encodedPassword);
        user.setRole(form.getRole());

        userRepository.save(user);
    }

    // ==============================
    // Update user
    // ==============================

    public void updateUser(Long id, UserUpdateForm form) {
        User existing = userRepository.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("User not found");
        }
        if (!existing.getEmail().equals(form.getEmail()) &&
                userRepository.existsByEmail(form.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        existing.setUsername(form.getUsername());
        existing.setEmail(form.getEmail());
        existing.setRole(form.getRole());

        userRepository.update(existing);
    }

    // ==============================
    // Delete user
    // ==============================

    public void deleteUser(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.delete(id);
    }

    // ==============================
    // Authenticate
    // ==============================

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) return null;
        if (!passwordEncoder.matches(password, user.getPasswordHash())) return null;
        return user;
    }
}