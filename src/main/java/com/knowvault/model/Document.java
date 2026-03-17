package com.knowvault.model;

import java.time.LocalDateTime;

/**
 * Document - Entity representing an uploaded document.
 * Extends BaseEntity to inherit common fields (id, createdAt, updatedAt).
 *
 * @author Sebastián González Tabares
 */
public class Document extends BaseEntity {

    private String title;
    private String description;
    private String originalFileName;
    private String storedFileName;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private Long categoryId;
    private String categoryName;
    private Long uploadedBy;
    private String status;
    private LocalDateTime uploadDate;
    private LocalDateTime lastAccessed;

    // ==============================
    // Implement abstract method
    // ==============================

    @Override
    public Long getId() {
        return id;
    }

    // Alias for compatibility
    public Long getDocumentId() {
        return id;
    }

    public void setDocumentId(Long documentId) {
        this.id = documentId;
    }

    // ==============================
    // Getters and Setters
    // ==============================

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(Long uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public LocalDateTime getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(LocalDateTime lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}