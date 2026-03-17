package com.knowvault.repository;

import java.util.List;

import com.knowvault.model.Document;

/**
 * DocumentRepository - Interface defining the contract for document data access.
 * JdbcDocumentRepository provides the concrete JDBC implementation.
 *
 * @author Sebastián González Tabares
 */
public interface DocumentRepository {

    void insert(String title, String originalFileName, String storedFileName,
                String filePath, long fileSize, String mimeType, Long uploadedBy);

    List<Document> findAll();

    Document findById(Long id);

    void delete(Long id);

    List<Document> search(String query);

    List<Document> findRecent(int limit);

    long sumFileSizes();

    int countDocuments();
}