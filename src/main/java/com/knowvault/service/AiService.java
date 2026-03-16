package com.knowvault.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.knowvault.model.DocumentChunk;
import com.knowvault.repository.JdbcDocumentChunkRepository;

/**
 * AiService - Core RAG pipeline service.
 * Extracts keywords → searches chunks → builds prompt → calls Claude API.
 *
 * @author Kevin García Gutiérrez
 */
@Service
public class AiService {

    private final JdbcDocumentChunkRepository chunkRepository;
    private final ObjectMapper objectMapper;

    @Value("${anthropic.api.key}")
    private String apiKey;

    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String CLAUDE_MODEL    = "claude-haiku-4-5-20251001";
    private static final int    MAX_CHUNKS      = 5;

    // Common words to ignore when extracting keywords
    private static final List<String> STOP_WORDS = Arrays.asList(
            "the", "a", "an", "is", "are", "was", "were", "be", "been",
            "have", "has", "had", "do", "does", "did", "will", "would",
            "could", "should", "may", "might", "shall", "can", "need",
            "what", "when", "where", "who", "which", "how", "why",
            "this", "that", "these", "those", "it", "its", "in", "on",
            "at", "to", "for", "of", "and", "or", "but", "not", "with",
            "from", "by", "about", "as", "into", "through", "during",
            "my", "your", "his", "her", "our", "their", "i", "you",
            "he", "she", "we", "they", "me", "him", "us", "them"
    );

    public AiService(JdbcDocumentChunkRepository chunkRepository) {
        this.chunkRepository = chunkRepository;
        this.objectMapper = new ObjectMapper();
    }

    // ==============================
    // Main method: process a user query
    // ==============================

    /**
     * Processes a natural language question through the RAG pipeline.
     *
     * @param userQuestion the question submitted by the user
     * @return AiResponse with answer text and source document IDs
     */
    public AiResponse processQuery(String userQuestion) {

        long startTime = System.currentTimeMillis();

        // Step 1: Extract keywords from the question
        List<String> keywords = extractKeywords(userQuestion);

        // Step 2: Search relevant chunks from document_chunks
        List<DocumentChunk> relevantChunks = chunkRepository.searchByKeywords(keywords, MAX_CHUNKS);

        // Step 3: Build response
        String answer;
        List<Long> documentIds = new ArrayList<>();

        if (relevantChunks.isEmpty()) {
            // No relevant documents found
            answer = "I couldn't find information related to your question in the uploaded documents. " +
                     "Please make sure relevant documents have been uploaded to the system.";
        } else {
            // Collect document IDs used
            documentIds = relevantChunks.stream()
                    .map(DocumentChunk::getDocumentId)
                    .distinct()
                    .collect(Collectors.toList());

            // Step 4: Build context from chunks
            String context = buildContext(relevantChunks);

            // Step 5: Call Claude API
            answer = callClaudeApi(userQuestion, context);
        }

        long executionTimeMs = System.currentTimeMillis() - startTime;

        return new AiResponse(answer, documentIds, (int) executionTimeMs);
    }

    // ==============================
    // Step 1: Extract keywords
    // ==============================

    private List<String> extractKeywords(String question) {
        return Arrays.stream(question.toLowerCase().split("\\s+"))
                .map(word -> word.replaceAll("[^a-záéíóúñ]", ""))
                .filter(word -> word.length() > 3)
                .filter(word -> !STOP_WORDS.contains(word))
                .distinct()
                .limit(6)
                .collect(Collectors.toList());
    }

    // ==============================
    // Step 2: Build context from chunks
    // ==============================

    private String buildContext(List<DocumentChunk> chunks) {
        StringBuilder context = new StringBuilder();

        for (int i = 0; i < chunks.size(); i++) {
            DocumentChunk chunk = chunks.get(i);
            context.append("--- Source ")
                   .append(i + 1)
                   .append(" (Document: ")
                   .append(chunk.getDocumentTitle() != null ? chunk.getDocumentTitle() : "Unknown")
                   .append(") ---\n")
                   .append(chunk.getChunkText())
                   .append("\n\n");
        }

        return context.toString();
    }

    // ==============================
    // Step 3: Call Claude API
    // ==============================

    private String callClaudeApi(String question, String context) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            // Build the prompt
            String systemPrompt = """
                    You are KnowVault AI, an enterprise knowledge assistant.
                    Your job is to answer questions ONLY based on the document context provided.
                    If the answer is not in the context, say so clearly.
                    Always be concise, accurate, and cite which source document you used.
                    Respond in the same language the user used to ask the question.
                    """;

            String userMessage = "Context from documents:\n\n" + context +
                                 "\n\nQuestion: " + question;

            // Build JSON body using Jackson
            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", CLAUDE_MODEL);
            body.put("max_tokens", 1024);
            body.put("system", systemPrompt);

            ArrayNode messages = objectMapper.createArrayNode();
            ObjectNode message = objectMapper.createObjectNode();
            message.put("role", "user");
            message.put("content", userMessage);
            messages.add(message);
            body.set("messages", messages);

            String jsonBody = objectMapper.writeValueAsString(body);

            // Build HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CLAUDE_API_URL))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            // Send request
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            // Parse response
            if (response.statusCode() == 200) {
                JsonNode responseJson = objectMapper.readTree(response.body());
                return responseJson
                        .path("content")
                        .get(0)
                        .path("text")
                        .asText("No response received from AI.");
            } else {
                System.err.println("Claude API error: " + response.statusCode() + " — " + response.body());
                return "The AI service is temporarily unavailable. Please try again later.";
            }

        } catch (Exception e) {
            System.err.println("Error calling Claude API: " + e.getMessage());
            return "An error occurred while processing your question. Please try again.";
        }
    }

    // ==============================
    // Inner class: AiResponse
    // ==============================

    /**
     * Wraps the AI response with metadata for the controller.
     */
    public static class AiResponse {
        private final String answer;
        private final List<Long> documentIds;
        private final int executionTimeMs;

        public AiResponse(String answer, List<Long> documentIds, int executionTimeMs) {
            this.answer = answer;
            this.documentIds = documentIds;
            this.executionTimeMs = executionTimeMs;
        }

        public String getAnswer() { return answer; }
        public List<Long> getDocumentIds() { return documentIds; }
        public int getExecutionTimeMs() { return executionTimeMs; }

        public String getDocumentIdsAsJson() {
            if (documentIds == null || documentIds.isEmpty()) return "[]";
            return "[" + documentIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")) + "]";
        }
    }
}