package com.knowvault.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.knowvault.model.User;
import com.knowvault.model.dto.LoginForm;
import com.knowvault.model.dto.UserCreateForm;
import com.knowvault.model.dto.UserUpdateForm;
import com.knowvault.repository.JdbcUserRepository;

@Service
public class UserService {

    private final JdbcUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(JdbcUserRepository userRepository,
                    BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==============================
    // LOGIN
    // ==============================

    public User login(LoginForm form) {

        User user = userRepository.findByEmail(form.getEmail());

        if (user == null) {
            return null;
        }

        boolean passwordMatches =
                passwordEncoder.matches(form.getPassword(), user.getPasswordHash());

        if (!passwordMatches) {
            return null;
        }

        return user;
    }


    // ==============================
    // CREATE USER
    // ==============================

    public void createUser(UserCreateForm form) {

        if (userRepository.existsByEmail(form.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());

        String hash = passwordEncoder.encode(form.getPassword());
        user.setPasswordHash(hash);

        user.setRole("USER");

        userRepository.save(user);
    }


    // ==============================
    // UPDATE USER
    // ==============================

    public void updateUser(UserUpdateForm form) {

        User user = userRepository.findById(form.getUserId());

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());

        userRepository.update(user);
    }


    // ==============================
    // DELETE USER
    // ==============================

    public void deleteUser(Long id) {

        User user = userRepository.findById(id);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        userRepository.delete(id);
    }


    // ==============================
    // GET ALL USERS
    // ==============================

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    // ==============================
    // GET USER BY ID
    // ==============================

    public User getUserById(Long id) {
        return userRepository.findById(id);
    }

}