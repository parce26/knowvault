package com.knowvault.repository;

import java.util.List;

import com.knowvault.model.DocumentChunk;

/**
 * DocumentChunkRepository - Interface for document chunk data access.
 * Used by the RAG pipeline to store and retrieve text fragments.
 * JdbcDocumentChunkRepository provides the concrete JDBC implementation.
 *
 * @author Sebastián González Tabares
 */
public interface DocumentChunkRepository {

    void insert(DocumentChunk chunk);

    void insertAll(List<DocumentChunk> chunks);

    List<DocumentChunk> findByDocumentId(Long documentId);

    List<DocumentChunk> searchByKeywords(List<String> keywords, int limit);

    void deleteByDocumentId(Long documentId);

    int countByDocumentId(Long documentId);
}