package com.knowvault.model;

import java.time.LocalDateTime;

/**
 * DocumentChunk - Represents a text fragment extracted from a document.
 * Used by the RAG pipeline to search relevant content for AI queries.
 *
 * @author Kevin García Gutiérrez
 */
public class DocumentChunk {

    private Long chunkId;
    private Long documentId;
    private String chunkText;
    private Integer chunkOrder;
    private Integer pageNumber;
    private LocalDateTime createdAt;

    // Optional: document title joined from documents table
    private String documentTitle;

    // ==============================
    // Constructors
    // ==============================

    public DocumentChunk() {}

    public DocumentChunk(Long documentId, String chunkText, Integer chunkOrder, Integer pageNumber) {
        this.documentId = documentId;
        this.chunkText = chunkText;
        this.chunkOrder = chunkOrder;
        this.pageNumber = pageNumber;
    }

    // ==============================
    // Getters and Setters
    // ==============================

    public Long getChunkId() {
        return chunkId;
    }

    public void setChunkId(Long chunkId) {
        this.chunkId = chunkId;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getChunkText() {
        return chunkText;
    }

    public void setChunkText(String chunkText) {
        this.chunkText = chunkText;
    }

    public Integer getChunkOrder() {
        return chunkOrder;
    }

    public void setChunkOrder(Integer chunkOrder) {
        this.chunkOrder = chunkOrder;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }
}