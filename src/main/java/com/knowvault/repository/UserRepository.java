package com.knowvault.repository;

import java.util.List;

import com.knowvault.model.User;

public interface UserRepository {

    void save(User user);

    List<User> findAll();

    User findById(Long id);

    void update(User user);

    void deleteById(Long id);
}
