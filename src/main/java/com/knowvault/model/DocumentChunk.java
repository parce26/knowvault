package com.knowvault.model;

/**
 * DocumentChunk - Entity representing a text fragment from a document.
 * Extends BaseEntity to inherit common fields (id, createdAt, updatedAt).
 * Used by the RAG pipeline for semantic search.
 *
 * @author Sebastián González Tabares
 */
public class DocumentChunk extends BaseEntity {

    private Long documentId;
    private String chunkText;
    private Integer chunkOrder;
    private Integer pageNumber;
    private String documentTitle;

    // ==============================
    // Constructors
    // ==============================

    public DocumentChunk() {}

    public DocumentChunk(Long documentId, String chunkText,
                         Integer chunkOrder, Integer pageNumber) {
        this.documentId = documentId;
        this.chunkText = chunkText;
        this.chunkOrder = chunkOrder;
        this.pageNumber = pageNumber;
    }

    // ==============================
    // Implement abstract method
    // ==============================

    @Override
    public Long getId() {
        return id;
    }

    // Alias for compatibility
    public Long getChunkId() {
        return id;
    }

    public void setChunkId(Long chunkId) {
        this.id = chunkId;
    }

    // ==============================
    // Getters and Setters
    // ==============================

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

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }
}