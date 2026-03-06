package com.knowvault.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.knowvault.model.User;

@Repository
public class JdbcUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ==============================
    // Find user by ID
    // ==============================

    public User findById(Long id) {

        String sql = "SELECT * FROM users WHERE user_id = ?";

        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> mapUser(rs), id);

        return users.isEmpty() ? null : users.get(0);
    }


    // ==============================
    // Find user by email
    // ==============================

    public User findByEmail(String email) {

        String sql = "SELECT * FROM users WHERE email = ?";

        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> mapUser(rs), email);

        return users.isEmpty() ? null : users.get(0);
    }


    // ==============================
    // Find user by username
    // ==============================

    public User findByUsername(String username) {

        String sql = "SELECT * FROM users WHERE username = ?";

        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> mapUser(rs), username);

        return users.isEmpty() ? null : users.get(0);
    }


    // ==============================
    // Save new user
    // ==============================

    public void save(User user) {

        String sql = """
                INSERT INTO users
                (username, email, password_hash, role, created_at)
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

    public void update(User user) {

        String sql = """
                UPDATE users
                SET username = ?, email = ?, updated_at = NOW()
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

    public void delete(Long id) {

        String sql = "DELETE FROM users WHERE user_id = ?";

        jdbcTemplate.update(sql, id);
    }


    // ==============================
    // Get all users
    // ==============================

    public List<User> findAll() {

        String sql = "SELECT * FROM users";

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapUser(rs));
    }


    // ==============================
    // Check if email exists
    // ==============================

    public boolean existsByEmail(String email) {

        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);

        return count != null && count > 0;
    }


    // ==============================
    // Row mapper
    // ==============================

    private User mapUser(java.sql.ResultSet rs) throws java.sql.SQLException {

        User u = new User();

        u.setUserId(rs.getLong("user_id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));

        if (rs.getTimestamp("created_at") != null) {
            u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }

        if (rs.getTimestamp("updated_at") != null) {
            u.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        return u;
    }
}