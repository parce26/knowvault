package com.knowvault.service;

import com.knowvault.exception.DuplicateResourceException;
import com.knowvault.exception.ResourceNotFoundException;
import com.knowvault.model.User;
import com.knowvault.model.dto.UserCreateForm;
import com.knowvault.model.dto.UserUpdateForm;
import com.knowvault.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public User getUserOrThrow(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + userId));
    }

    public void createUser(UserCreateForm form) {
        // Unicidad
        userRepository.findByUsername(form.getUsername())
                .ifPresent(u -> { throw new DuplicateResourceException("Username ya existe"); });

        userRepository.findByEmail(form.getEmail())
                .ifPresent(u -> { throw new DuplicateResourceException("Email ya existe"); });

        User u = new User();
        u.setUsername(form.getUsername().trim());
        u.setEmail(form.getEmail().trim().toLowerCase());
        u.setRole(form.getRole().trim());

        String hash = encoder.encode(form.getPassword());
        u.setPasswordHash(hash);

        userRepository.save(u);
    }

    public void updateUser(int userId, UserUpdateForm form) {
        User existing = getUserOrThrow(userId);

        // Si cambian username/email, validar unicidad (excluyendo el mismo user)
        userRepository.findByUsername(form.getUsername())
                .ifPresent(u -> {
                    if (!u.getUserId().equals(userId)) {
                        throw new DuplicateResourceException("Username ya existe");
                    }
                });

        userRepository.findByEmail(form.getEmail())
                .ifPresent(u -> {
                    if (!u.getUserId().equals(userId)) {
                        throw new DuplicateResourceException("Email ya existe");
                    }
                });

        existing.setUsername(form.getUsername().trim());
        existing.setEmail(form.getEmail().trim().toLowerCase());
        existing.setRole(form.getRole().trim());

        // Password opcional
        String newPassword = (form.getPassword() == null) ? "" : form.getPassword().trim();
        if (!newPassword.isEmpty()) {
            existing.setPasswordHash(encoder.encode(newPassword));
        }

        boolean ok = userRepository.update(existing);
        if (!ok) {
            throw new ResourceNotFoundException("No se pudo actualizar (id no existe): " + userId);
        }
    }

    public void deleteUser(int userId) {
        boolean ok = userRepository.deleteById(userId);
        if (!ok) {
            throw new ResourceNotFoundException("No se pudo eliminar (id no existe): " + userId);
        }
    }
}