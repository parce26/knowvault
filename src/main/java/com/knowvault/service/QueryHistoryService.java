package com.knowvault.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.knowvault.model.QueryHistory;
import com.knowvault.repository.JdbcQueryHistoryRepository;

/**
 * QueryHistoryService - Business logic layer for the History module.
 * Acts as intermediary between HistoryController and JdbcQueryHistoryRepository.
 *
 * @author Kevin García Gutiérrez
 */
@Service
public class QueryHistoryService {

    private final JdbcQueryHistoryRepository historyRepository;

    public QueryHistoryService(JdbcQueryHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    // ==============================
    // Save a new query record
    // ==============================

    /**
     * Persists a completed AI query to the database.
     *
     * @param userId          the ID of the user who made the query
     * @param queryText       the question submitted by the user
     * @param responseText    the AI-generated response
     * @param documentsUsed   JSON string of document IDs referenced (can be null)
     * @param executionTimeMs response time in milliseconds (can be null)
     */
    public void saveQuery(Long userId, String queryText, String responseText,
                          String documentsUsed, Integer executionTimeMs) {
        QueryHistory history = new QueryHistory(
                userId, queryText, responseText, documentsUsed, executionTimeMs
        );
        historyRepository.insert(history);
    }

    // ==============================
    // Get all history for a user
    // ==============================

    public List<QueryHistory> getHistoryByUser(Long userId) {
        return historyRepository.findByUserId(userId);
    }

    // ==============================
    // Search history by keyword
    // ==============================

    public List<QueryHistory> searchHistory(Long userId, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return historyRepository.findByUserId(userId);
        }
        return historyRepository.searchByUser(userId, keyword);
    }

    // ==============================
    // Get recent entries for dashboard
    // ==============================

    public List<QueryHistory> getRecentHistory(Long userId, int limit) {
        return historyRepository.findRecentByUser(userId, limit);
    }

    // ==============================
    // Delete a single entry
    // ==============================

    public void deleteEntry(Long queryId) {
        historyRepository.deleteById(queryId);
    }

    // ==============================
    // Clear all history for a user
    // ==============================

    public void clearAllHistory(Long userId) {
        historyRepository.deleteAllByUserId(userId);
    }

    // ==============================
    // Count total queries for a user
    // ==============================

    public int getTotalQueries(Long userId) {
        return historyRepository.countByUserId(userId);
    }
}