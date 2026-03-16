package com.knowvault.repository;

import com.knowvault.model.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcDocumentRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcDocumentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Document> documentRowMapper = new RowMapper<>() {
        @Override
        public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
            Document doc = new Document();

            doc.setDocumentId(rs.getLong("document_id"));
            doc.setTitle(rs.getString("title"));
            doc.setOriginalFileName(rs.getString("original_filename"));
            doc.setStoredFileName(rs.getString("stored_filename"));
            doc.setFilePath(rs.getString("file_path"));
            doc.setFileSize(rs.getLong("file_size"));
            doc.setMimeType(rs.getString("mime_type"));
            doc.setCategoryId((Long) rs.getObject("category_id"));
            doc.setCategoryName(rs.getString("category_name"));
            doc.setUploadedBy(rs.getLong("uploaded_by"));
            doc.setStatus(rs.getString("status"));

            if (rs.getTimestamp("upload_date") != null) {
                doc.setUploadDate(rs.getTimestamp("upload_date").toLocalDateTime());
            }

            if (rs.getTimestamp("updated_at") != null) {
                doc.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }

            return doc;
        }
    };

    public void insert(
            String title,
            String originalFileName,
            String storedFileName,
            String filePath,
            long fileSize,
            String mimeType,
            Long uploadedBy
    ) {
        String sql = """
            INSERT INTO documents
            (title, original_filename, stored_filename, file_path, file_size, mime_type, uploaded_by, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, 'active')
            """;

        jdbcTemplate.update(
                sql,
                title,
                originalFileName,
                storedFileName,
                filePath,
                fileSize,
                mimeType,
                uploadedBy
        );
    }

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

    public Document findById(Long id) {
        String sql = """
            SELECT d.*, c.name AS category_name
            FROM documents d
            LEFT JOIN categories c ON d.category_id = c.category_id
            WHERE d.document_id = ?
            """;

        return jdbcTemplate.queryForObject(sql, documentRowMapper, id);
    }

    public void delete(Long id) {
        String sql = """
            UPDATE documents
            SET status = 'deleted'
            WHERE document_id = ?
            """;

        jdbcTemplate.update(sql, id);
    }

    public List<Document> search(String query) {
        String sql = """
            SELECT d.*, c.name AS category_name
            FROM documents d
            LEFT JOIN categories c ON d.category_id = c.category_id
            WHERE d.status <> 'deleted'
            AND LOWER(d.title) LIKE LOWER(?)
            ORDER BY d.upload_date DESC
            """;

        return jdbcTemplate.query(sql, documentRowMapper, "%" + query + "%");
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
        String sql = """
            SELECT COALESCE(SUM(file_size), 0)
            FROM documents
            WHERE status <> 'deleted'
            """;

        Long total = jdbcTemplate.queryForObject(sql, Long.class);
        return total != null ? total : 0L;
    }

    public int countDocuments() {
        String sql = """
            SELECT COUNT(*)
            FROM documents
            WHERE status <> 'deleted'
            """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
}