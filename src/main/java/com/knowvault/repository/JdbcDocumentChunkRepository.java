package com.knowvault.repository;

import com.knowvault.model.DocumentChunk;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * JdbcDocumentChunkRepository - Data access for document_chunks table.
 * Supports RAG pipeline: insert chunks and search by keyword.
 *
 * @author Kevin García Gutiérrez
 */
@Repository
public class JdbcDocumentChunkRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcDocumentChunkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ==============================
    // RowMapper
    // ==============================

    private final RowMapper<DocumentChunk> chunkRowMapper = new RowMapper<>() {
        @Override
        public DocumentChunk mapRow(ResultSet rs, int rowNum) throws SQLException {
            DocumentChunk chunk = new DocumentChunk();
            chunk.setChunkId(rs.getLong("chunk_id"));
            chunk.setDocumentId(rs.getLong("document_id"));
            chunk.setChunkText(rs.getString("chunk_text"));
            chunk.setChunkOrder(rs.getInt("chunk_order"));
            chunk.setPageNumber(rs.getObject("page_number", Integer.class));

            if (rs.getTimestamp("created_at") != null) {
                chunk.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }

            // Try to get document title if joined
            try {
                chunk.setDocumentTitle(rs.getString("title"));
            } catch (SQLException ignored) {}

            return chunk;
        }
    };

    // ==============================
    // INSERT - Save a chunk
    // ==============================

    public void insert(DocumentChunk chunk) {
        String sql = """
                INSERT INTO document_chunks
                (document_id, chunk_text, chunk_order, page_number)
                VALUES (?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                chunk.getDocumentId(),
                chunk.getChunkText(),
                chunk.getChunkOrder(),
                chunk.getPageNumber()
        );
    }

    // ==============================
    // INSERT BATCH - Save all chunks for a document
    // ==============================

    public void insertAll(List<DocumentChunk> chunks) {
        for (DocumentChunk chunk : chunks) {
            insert(chunk);
        }
    }

    // ==============================
    // SELECT - Find chunks by document
    // ==============================

    public List<DocumentChunk> findByDocumentId(Long documentId) {
        String sql = """
                SELECT * FROM document_chunks
                WHERE document_id = ?
                ORDER BY chunk_order ASC
                """;
        return jdbcTemplate.query(sql, chunkRowMapper, documentId);
    }

    // ==============================
    // SELECT - Search chunks by keyword (RAG retrieval)
    // Uses LIKE search — works without FULLTEXT index
    // ==============================

    public List<DocumentChunk> searchByKeyword(String keyword, int limit) {
        String sql = """
                SELECT dc.*, d.title
                FROM document_chunks dc
                JOIN documents d ON dc.document_id = d.document_id
                WHERE d.status <> 'deleted'
                AND LOWER(dc.chunk_text) LIKE LOWER(?)
                LIMIT ?
                """;
        return jdbcTemplate.query(sql, chunkRowMapper, "%" + keyword + "%", limit);
    }

    // ==============================
    // SELECT - Search by multiple keywords (better RAG)
    // ==============================

    public List<DocumentChunk> searchByKeywords(List<String> keywords, int limit) {
        if (keywords == null || keywords.isEmpty()) {
            return List.of();
        }

        // Build WHERE clause: chunk_text LIKE %word1% OR chunk_text LIKE %word2% ...
        StringBuilder conditions = new StringBuilder();
        Object[] params = new Object[keywords.size() + 1];

        for (int i = 0; i < keywords.size(); i++) {
            if (i > 0) conditions.append(" OR ");
            conditions.append("LOWER(dc.chunk_text) LIKE LOWER(?)");
            params[i] = "%" + keywords.get(i) + "%";
        }
        params[keywords.size()] = limit;

        String sql = """
                SELECT dc.*, d.title
                FROM document_chunks dc
                JOIN documents d ON dc.document_id = d.document_id
                WHERE d.status <> 'deleted'
                AND (
                """ + conditions + """
                )
                LIMIT ?
                """;

        return jdbcTemplate.query(sql, chunkRowMapper, params);
    }

    // ==============================
    // DELETE - Remove all chunks for a document
    // ==============================

    public void deleteByDocumentId(Long documentId) {
        String sql = "DELETE FROM document_chunks WHERE document_id = ?";
        jdbcTemplate.update(sql, documentId);
    }

    // ==============================
    // COUNT - How many chunks a document has
    // ==============================

    public int countByDocumentId(Long documentId) {
        String sql = "SELECT COUNT(*) FROM document_chunks WHERE document_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, documentId);
        return count != null ? count : 0;
    }
}