package com.knowvault.repository;

import com.knowvault.model.QueryHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * JdbcQueryHistoryRepository - Data access layer for the query_history table.
 * Uses Spring JdbcTemplate with prepared statements (SQL injection safe).
 *
 * @author Kevin García Gutiérrez
 */
@Repository
public class JdbcQueryHistoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcQueryHistoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ==============================
    // RowMapper
    // ==============================

    private final RowMapper<QueryHistory> historyRowMapper = new RowMapper<>() {
        @Override
        public QueryHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
            QueryHistory h = new QueryHistory();

            h.setQueryId(rs.getLong("query_id"));
            h.setUserId(rs.getLong("user_id"));
            h.setQueryText(rs.getString("query_text"));
            h.setResponseText(rs.getString("response_text"));
            h.setDocumentsUsed(rs.getString("documents_used"));
            h.setExecutionTimeMs(rs.getObject("execution_time_ms", Integer.class));

            if (rs.getTimestamp("created_at") != null) {
                h.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }

            return h;
        }
    };

    // ==============================
    // INSERT - Save a new query
    // ==============================

    public void insert(QueryHistory history) {
        String sql = """
                INSERT INTO query_history
                (user_id, query_text, response_text, documents_used, execution_time_ms)
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                history.getUserId(),
                history.getQueryText(),
                history.getResponseText(),
                history.getDocumentsUsed(),
                history.getExecutionTimeMs()
        );
    }

    // ==============================
    // SELECT - Find all by user
    // ==============================

    public List<QueryHistory> findByUserId(Long userId) {
        String sql = """
                SELECT *
                FROM query_history
                WHERE user_id = ?
                ORDER BY created_at DESC
                """;

        return jdbcTemplate.query(sql, historyRowMapper, userId);
    }

    // ==============================
    // SELECT - Find by ID
    // ==============================

    public QueryHistory findById(Long queryId) {
        String sql = "SELECT * FROM query_history WHERE query_id = ?";

        List<QueryHistory> results = jdbcTemplate.query(sql, historyRowMapper, queryId);

        return results.isEmpty() ? null : results.get(0);
    }

    // ==============================
    // SELECT - Search by keyword (for a specific user)
    // ==============================

    public List<QueryHistory> searchByUser(Long userId, String keyword) {
        String sql = """
                SELECT *
                FROM query_history
                WHERE user_id = ?
                AND LOWER(query_text) LIKE LOWER(?)
                ORDER BY created_at DESC
                """;

        return jdbcTemplate.query(sql, historyRowMapper, userId, "%" + keyword + "%");
    }

    // ==============================
    // SELECT - Recent entries for a user
    // ==============================

    public List<QueryHistory> findRecentByUser(Long userId, int limit) {
        String sql = """
                SELECT *
                FROM query_history
                WHERE user_id = ?
                ORDER BY created_at DESC
                LIMIT ?
                """;

        return jdbcTemplate.query(sql, historyRowMapper, userId, limit);
    }

    // ==============================
    // DELETE - Single entry
    // ==============================

    public void deleteById(Long queryId) {
        String sql = "DELETE FROM query_history WHERE query_id = ?";
        jdbcTemplate.update(sql, queryId);
    }

    // ==============================
    // DELETE - All entries for a user
    // ==============================

    public void deleteAllByUserId(Long userId) {
        String sql = "DELETE FROM query_history WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    // ==============================
    // COUNT - Total queries by user
    // ==============================

    public int countByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM query_history WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }
}
