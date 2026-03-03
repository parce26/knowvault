package com.knowvault.repository;

import com.knowvault.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private User mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        if (created != null) u.setCreatedAt(created.toLocalDateTime());
        if (updated != null) u.setUpdatedAt(updated.toLocalDateTime());

        return u;
    }

    @Override
    public List<User> findAll() {
        String sql = """
                SELECT user_id, username, email, password_hash, role, created_at, updated_at
                FROM users
                ORDER BY user_id DESC
                """;
        return jdbcTemplate.query(sql, this::mapRow);
    }

    @Override
    public Optional<User> findById(int userId) {
        String sql = """
                SELECT user_id, username, email, password_hash, role, created_at, updated_at
                FROM users
                WHERE user_id = ?
                """;
        try {
            User u = jdbcTemplate.queryForObject(sql, this::mapRow, userId);
            return Optional.ofNullable(u);
        } catch (DataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = """
                SELECT user_id, username, email, password_hash, role, created_at, updated_at
                FROM users
                WHERE username = ?
                """;
        try {
            User u = jdbcTemplate.queryForObject(sql, this::mapRow, username);
            return Optional.ofNullable(u);
        } catch (DataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = """
                SELECT user_id, username, email, password_hash, role, created_at, updated_at
                FROM users
                WHERE email = ?
                """;
        try {
            User u = jdbcTemplate.queryForObject(sql, this::mapRow, email);
            return Optional.ofNullable(u);
        } catch (DataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public int save(User user) {
        String sql = """
                INSERT INTO users (username, email, password_hash, role)
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return (key == null) ? 0 : key.intValue();
    }

    @Override
    public boolean update(User user) {
        String sql = """
                UPDATE users
                SET username = ?, email = ?, password_hash = ?, role = ?
                WHERE user_id = ?
                """;
        int updated = jdbcTemplate.update(sql,
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                user.getUserId()
        );
        return updated == 1;
    }

    @Override
    public boolean deleteById(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        int deleted = jdbcTemplate.update(sql, userId);
        return deleted == 1;
    }
}