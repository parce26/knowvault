package com.knowvault.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.knowvault.model.User;

/**
 * JdbcUserRepository - JDBC implementation of UserRepository interface.
 * Uses Spring JdbcTemplate with prepared statements for all database operations.
 *
 * @author Sebastián González Tabares
 */
@Repository
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ==============================
    // RowMapper helper
    // ==============================

    private User mapUser(java.sql.ResultSet rs) throws java.sql.SQLException {
        User user = new User();
        user.setUserId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));

        try { user.setFullName(rs.getString("full_name")); }
        catch (java.sql.SQLException ignored) {}

        if (rs.getTimestamp("created_at") != null)
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        if (rs.getTimestamp("updated_at") != null)
            user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return user;
    }

    // ==============================
    // Find by ID
    // ==============================

    @Override
    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> mapUser(rs), id);
        return users.isEmpty() ? null : users.get(0);
    }

    // ==============================
    // Find by email
    // ==============================

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> mapUser(rs), email);
        return users.isEmpty() ? null : users.get(0);
    }

    // ==============================
    // Find by username
    // ==============================

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> mapUser(rs), username);
        return users.isEmpty() ? null : users.get(0);
    }

    // ==============================
    // Find all
    // ==============================

    public ArrayList<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNum) -> mapUser(rs)));
    }

    // ==============================
    // Save new user
    // ==============================

    @Override
    public void save(User user) {
        String sql = """
                INSERT INTO users (username, email, password_hash, role, created_at)
                VALUES (?, ?, ?, ?, NOW())
                """;
        jdbcTemplate.update(sql,
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole());
    }

    // ==============================
    // Update user
    // ==============================

    @Override
    public void update(User user) {
        String sql = """
                UPDATE users SET username = ?, email = ?, updated_at = NOW()
                WHERE user_id = ?
                """;
        jdbcTemplate.update(sql,
                user.getUsername(),
                user.getEmail(),
                user.getUserId());
    }

    // ==============================
    // Delete user
    // ==============================

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, id);
    }

    // ==============================
    // Existence checks
    // ==============================

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }
}