package com.knowvault.model;

import java.time.format.DateTimeFormatter;

/**
 * QueryHistory - Entity representing an AI query interaction.
 * Extends BaseEntity to inherit common fields (id, createdAt, updatedAt).
 *
 * @author Sebastián González Tabares
 */
public class QueryHistory extends BaseEntity {

    private Long userId;
    private String queryText;
    private String responseText;
    private String documentsUsed;
    private Integer executionTimeMs;

    // ==============================
    // Constructors
    // ==============================

    public QueryHistory() {}

    public QueryHistory(Long userId, String queryText, String responseText,
                        String documentsUsed, Integer executionTimeMs) {
        this.userId = userId;
        this.queryText = queryText;
        this.responseText = responseText;
        this.documentsUsed = documentsUsed;
        this.executionTimeMs = executionTimeMs;
    }

    // ==============================
    // Implement abstract method
    // ==============================

    @Override
    public Long getId() {
        return id;
    }

    // Alias for compatibility
    public Long getQueryId() {
        return id;
    }

    public void setQueryId(Long queryId) {
        this.id = queryId;
    }

    // ==============================
    // Display helper methods
    // ==============================

    public String getDisplayDay() {
        if (createdAt == null) return "--";
        return String.valueOf(createdAt.getDayOfMonth());
    }

    public String getDisplayMonth() {
        if (createdAt == null) return "--";
        return createdAt.format(DateTimeFormatter.ofPattern("MMM"));
    }

    public String getDisplayTime() {
        if (createdAt == null) return "--:--";
        return createdAt.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getResponsePreview() {
        if (responseText == null) return "";
        if (responseText.length() <= 200) return responseText;
        return responseText.substring(0, 200) + "...";
    }

    // ==============================
    // Getters and Setters
    // ==============================

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getDocumentsUsed() {
        return documentsUsed;
    }

    public void setDocumentsUsed(String documentsUsed) {
        this.documentsUsed = documentsUsed;
    }

    public Integer getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Integer executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
}