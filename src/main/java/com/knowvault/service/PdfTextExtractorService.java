package com.knowvault.service;

import com.knowvault.model.DocumentChunk;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PdfTextExtractorService - Extracts text from PDF files and splits into chunks.
 * Used during document upload to populate document_chunks table for RAG.
 * Compatible with Apache PDFBox 3.x
 *
 * @author Kevin García Gutiérrez
 */
@Service
public class PdfTextExtractorService {

    // Target chunk size in words (~400 words per chunk)
    private static final int CHUNK_SIZE_WORDS = 400;

    // ==============================
    // Extract text from PDF file
    // ==============================

    /**
     * Extracts all text from a PDF file at the given path.
     *
     * @param filePath full path to the PDF file
     * @return extracted text as a single string, or empty string if failed
     */
    public String extractText(String filePath) {
        try (PDDocument document = Loader.loadPDF(new File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            System.err.println("PDF extraction failed for: " + filePath + " — " + e.getMessage());
            return "";
        }
    }

    // ==============================
    // Split text into chunks
    // ==============================

    /**
     * Splits extracted text into DocumentChunk objects of ~400 words each.
     *
     * @param text       full extracted text
     * @param documentId the document this text belongs to
     * @return list of DocumentChunk objects ready to insert into DB
     */
    public List<DocumentChunk> splitIntoChunks(String text, Long documentId) {
        List<DocumentChunk> chunks = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return chunks;
        }

        String[] words = text.trim().split("\\s+");

        int chunkOrder = 0;
        StringBuilder currentChunk = new StringBuilder();
        int wordCount = 0;

        for (String word : words) {
            currentChunk.append(word).append(" ");
            wordCount++;

            if (wordCount >= CHUNK_SIZE_WORDS) {
                chunks.add(new DocumentChunk(
                        documentId,
                        currentChunk.toString().trim(),
                        chunkOrder,
                        null
                ));
                chunkOrder++;
                currentChunk = new StringBuilder();
                wordCount = 0;
            }
        }

        // Remaining text as last chunk
        if (!currentChunk.isEmpty()) {
            chunks.add(new DocumentChunk(
                    documentId,
                    currentChunk.toString().trim(),
                    chunkOrder,
                    null
            ));
        }

        return chunks;
    }

    // ==============================
    // Full pipeline: extract + split
    // ==============================

    /**
     * Convenience method: extract text from PDF and return ready-to-insert chunks.
     *
     * @param filePath   full path to the PDF
     * @param documentId the document ID to associate chunks with
     * @return list of chunks, empty if PDF has no extractable text
     */
    public List<DocumentChunk> extractAndChunk(String filePath, Long documentId) {
        String text = extractText(filePath);
        return splitIntoChunks(text, documentId);
    }
}