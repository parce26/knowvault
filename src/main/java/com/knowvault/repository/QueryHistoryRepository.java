package com.knowvault.repository;

import java.util.List;

import com.knowvault.model.QueryHistory;

/**
 * QueryHistoryRepository - Interface for query history data access.
 * JdbcQueryHistoryRepository provides the concrete JDBC implementation.
 *
 * @author Sebastián González Tabares
 */
public interface QueryHistoryRepository {

    void insert(QueryHistory history);

    List<QueryHistory> findByUserId(Long userId);

    QueryHistory findById(Long queryId);

    List<QueryHistory> searchByUser(Long userId, String keyword);

    List<QueryHistory> findRecentByUser(Long userId, int limit);

    void deleteById(Long queryId);

    void deleteAllByUserId(Long userId);

    int countByUserId(Long userId);
}