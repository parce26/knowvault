package com.knowvault.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * QueryHistory - Represents a single AI query record in the system.
 * Maps directly to the query_history table in the database.
 *
 * @author Kevin García Gutiérrez
 */
public class QueryHistory {

    // ==============================
    // Fields
    // ==============================

    private Long queryId;
    private Long userId;
    private String queryText;
    private String responseText;
    private String documentsUsed;   // stored as JSON string in DB
    private Integer executionTimeMs;
    private LocalDateTime createdAt;

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
    // Helper methods
    // ==============================

    /**
     * Returns the day-of-month for display in the timeline (e.g., "26").
     */
    public String getDisplayDay() {
        if (createdAt == null) return "--";
        return String.valueOf(createdAt.getDayOfMonth());
    }

    /**
     * Returns the abbreviated month for display in the timeline (e.g., "Feb").
     */
    public String getDisplayMonth() {
        if (createdAt == null) return "--";
        return createdAt.format(DateTimeFormatter.ofPattern("MMM"));
    }

    /**
     * Returns the time formatted as HH:mm for display (e.g., "14:30").
     */
    public String getDisplayTime() {
        if (createdAt == null) return "--:--";
        return createdAt.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /**
     * Returns a truncated preview of the response (max 200 chars).
     */
    public String getResponsePreview() {
        if (responseText == null) return "";
        if (responseText.length() <= 200) return responseText;
        return responseText.substring(0, 200) + "...";
    }

    // ==============================
    // Getters and Setters
    // ==============================

    public Long getQueryId() {
        return queryId;
    }

    public void setQueryId(Long queryId) {
        this.queryId = queryId;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}