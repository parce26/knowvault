package com.knowvault.service;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.knowvault.model.Category;
import com.knowvault.model.Document;
import com.knowvault.repository.JdbcDocumentRepository;

/**
 * DocumentService - business logic for document management.
 *
 * @author Sebastián González Tabares
 */
@Service
public class DocumentService {

    private final JdbcDocumentRepository documentRepository;
    private final JdbcTemplate jdbcTemplate;

    public DocumentService(
            JdbcDocumentRepository documentRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.documentRepository = documentRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    // ── Document CRUD ──────────────────────────────────────────────────────

    public void createDocument(
            String title,
            String originalFileName,
            String storedFileName,
            String filePath,
            long fileSize,
            String mimeType,
            String category,
            Long uploadedBy
    ) {
        documentRepository.insert(
                title,
                originalFileName,
                storedFileName,
                filePath,
                fileSize,
                mimeType,
                category,
                uploadedBy
        );
    }

    // Keep old signature for backward compatibility
    public void createDocument(
            String title,
            String originalFileName,
            String storedFileName,
            String filePath,
            long fileSize,
            String mimeType,
            Long uploadedBy
    ) {
        documentRepository.insert(
                title,
                originalFileName,
                storedFileName,
                filePath,
                fileSize,
                mimeType,
                null,
                uploadedBy
        );
    }

    public List<Document> searchDocuments(String query) {
        if (query == null || query.isBlank()) {
            return documentRepository.findAll();
        }
        return documentRepository.search(query);
    }

    public Document getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    public void deleteDocument(Long id) {
        documentRepository.delete(id);
    }

    public List<Document> getRecentDocuments(int limit) {
        return documentRepository.findRecent(limit);
    }

    public long getTotalStorageUsedBytes() {
        return documentRepository.sumFileSizes();
    }

    public int getTotalDocuments() {
        return documentRepository.countDocuments();
    }

    public String formatStorageSize(long bytes) {
        double kb = bytes / 1024.0;
        double mb = kb / 1024.0;
        double gb = mb / 1024.0;

        if (gb >= 1) return String.format("%.2f GB", gb);
        if (mb >= 1) return String.format("%.2f MB", mb);
        if (kb >= 1) return String.format("%.2f KB", kb);
        return bytes + " B";
    }

    // ── Categories ─────────────────────────────────────────────────────────

    public List<Category> getAllCategories() {
        String sql = "SELECT category_id, name, description FROM categories ORDER BY name ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Category cat = new Category();
            cat.setCategoryId(rs.getInt("category_id"));
            cat.setName(rs.getString("name"));
            cat.setDescription(rs.getString("description"));
            return cat;
        });
    }
}