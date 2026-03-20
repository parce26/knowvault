package com.knowvault.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.knowvault.model.Document;

/**
 * JdbcDocumentRepository - JDBC implementation of DocumentRepository.
 *
 * @author Sebastián González Tabares
 */
@Repository
public class JdbcDocumentRepository implements DocumentRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Document> documentRowMapper = (rs, rowNum) -> {
        Document doc = new Document();
        doc.setDocumentId(rs.getLong("document_id"));
        doc.setTitle(rs.getString("title"));
        doc.setOriginalFileName(rs.getString("original_filename"));
        doc.setStoredFileName(rs.getString("stored_filename"));
        doc.setFilePath(rs.getString("file_path"));
        doc.setFileSize(rs.getLong("file_size"));
        doc.setMimeType(rs.getString("mime_type"));
        doc.setUploadedBy(rs.getLong("uploaded_by"));
        doc.setStatus(rs.getString("status"));
        doc.setUploadDate(rs.getTimestamp("upload_date") != null
                ? rs.getTimestamp("upload_date").toLocalDateTime() : null);
        doc.setUpdatedAt(rs.getTimestamp("updated_at") != null
                ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
        doc.setCategoryName(rs.getString("category_name"));
        return doc;
    };

    public JdbcDocumentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ── Insert with category name ──────────────────────────────────────────

    public void insert(
            String title,
            String originalFileName,
            String storedFileName,
            String filePath,
            long fileSize,
            String mimeType,
            String category,
            Long uploadedBy
    ) {
        if (category == null || category.isBlank()) {
            // No category selected
            String sql = """
                INSERT INTO documents
                    (title, original_filename, stored_filename, file_path,
                     file_size, mime_type, uploaded_by, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, 'active')
                """;
            jdbcTemplate.update(sql, title, originalFileName, storedFileName,
                    filePath, fileSize, mimeType, uploadedBy);
        } else {
            // Resolve category name to ID
            String sql = """
                INSERT INTO documents
                    (title, original_filename, stored_filename, file_path,
                     file_size, mime_type, category_id, uploaded_by, status)
                VALUES (?, ?, ?, ?, ?, ?,
                    (SELECT category_id FROM categories WHERE name = ? LIMIT 1),
                    ?, 'active')
                """;
            jdbcTemplate.update(sql, title, originalFileName, storedFileName,
                    filePath, fileSize, mimeType, category, uploadedBy);
        }
    }

    // ── Keep original insert without category for backward compatibility ───

    @Override
    public void insert(
            String title,
            String originalFileName,
            String storedFileName,
            String filePath,
            long fileSize,
            String mimeType,
            Long uploadedBy
    ) {
        insert(title, originalFileName, storedFileName, filePath,
                fileSize, mimeType, null, uploadedBy);
    }

    @Override
    public List<Document> findAll() {
        String sql = """
            SELECT d.*, c.name AS category_name
            FROM documents d
            LEFT JOIN categories c ON d.category_id = c.category_id
            WHERE d.status <> 'deleted'
            ORDER BY d.upload_date DESC
            """;
        return jdbcTemplate.query(sql, documentRowMapper);
    }

    @Override
    public Document findById(Long id) {
        String sql = """
            SELECT d.*, c.name AS category_name
            FROM documents d
            LEFT JOIN categories c ON d.category_id = c.category_id
            WHERE d.document_id = ?
            """;
        return jdbcTemplate.queryForObject(sql, documentRowMapper, id);
    }

    public boolean update(Document document) {
        String sql = """
            UPDATE documents
            SET title = ?, updated_at = CURRENT_TIMESTAMP
            WHERE document_id = ?
            """;
        int rows = jdbcTemplate.update(sql, document.getTitle(), document.getDocumentId());
        return rows > 0;
    }

    public boolean deleteById(Long id) {
        String sql = "UPDATE documents SET status = 'deleted' WHERE document_id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }

    public void delete(Long id) {
        deleteById(id);
    }

    @Override
    public List<Document> search(String keyword) {
        String sql = """
            SELECT d.*, c.name AS category_name
            FROM documents d
            LEFT JOIN categories c ON d.category_id = c.category_id
            WHERE d.status <> 'deleted'
            AND LOWER(d.title) LIKE LOWER(?)
            ORDER BY d.upload_date DESC
            """;
        return jdbcTemplate.query(sql, documentRowMapper, "%" + keyword + "%");
    }

    public List<Document> findRecent(int limit) {
        String sql = """
            SELECT d.*, c.name AS category_name
            FROM documents d
            LEFT JOIN categories c ON d.category_id = c.category_id
            WHERE d.status <> 'deleted'
            ORDER BY d.upload_date DESC
            LIMIT ?
            """;
        return jdbcTemplate.query(sql, documentRowMapper, limit);
    }

    public long sumFileSizes() {
        String sql = "SELECT COALESCE(SUM(file_size), 0) FROM documents WHERE status <> 'deleted'";
        Long total = jdbcTemplate.queryForObject(sql, Long.class);
        return total != null ? total : 0L;
    }

    public int countDocuments() {
        String sql = "SELECT COUNT(*) FROM documents WHERE status <> 'deleted'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
}