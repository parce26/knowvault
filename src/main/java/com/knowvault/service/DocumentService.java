package com.knowvault.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.knowvault.model.Document;
import com.knowvault.repository.JdbcDocumentRepository;

@Service
public class DocumentService {

    private final JdbcDocumentRepository documentRepository;

    public DocumentService(JdbcDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

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

        if (gb >= 1) {
            return String.format("%.2f GB", gb);
        }
        if (mb >= 1) {
            return String.format("%.2f MB", mb);
        }
        if (kb >= 1) {
            return String.format("%.2f KB", kb);
        }
        return bytes + " B";
    }
}