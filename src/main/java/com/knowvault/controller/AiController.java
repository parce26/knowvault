package com.knowvault.controller;

import com.knowvault.model.User;
import com.knowvault.service.AiService;
import com.knowvault.service.QueryHistoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * AiController - REST endpoint for AI-powered queries.
 * POST /api/ask — receives question, returns AI answer as JSON.
 *
 * @author Kevin García Gutiérrez
 */
@RestController
@RequestMapping("/api")
public class AiController {

    private final AiService aiService;
    private final QueryHistoryService queryHistoryService;

    public AiController(AiService aiService, QueryHistoryService queryHistoryService) {
        this.aiService = aiService;
        this.queryHistoryService = queryHistoryService;
    }

    // ==============================
    // POST /api/ask
    // Receives: { "question": "What is the vacation policy?" }
    // Returns:  { "answer": "...", "documentIds": [...], "executionTimeMs": 1200 }
    // ==============================

    @PostMapping("/ask")
    public ResponseEntity<?> ask(
            @RequestBody Map<String, String> body,
            HttpSession session
    ) {
        // Session check
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Unauthorized. Please log in."));
        }

        // Validate input
        String question = body.get("question");
        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Question cannot be empty."));
        }

        // Process through RAG pipeline
        AiService.AiResponse aiResponse = aiService.processQuery(question);

        // Save to query_history
        queryHistoryService.saveQuery(
                user.getUserId(),
                question,
                aiResponse.getAnswer(),
                aiResponse.getDocumentIdsAsJson(),
                aiResponse.getExecutionTimeMs()
        );

        // Return JSON response to frontend
        return ResponseEntity.ok(Map.of(
                "answer", aiResponse.getAnswer(),
                "documentIds", aiResponse.getDocumentIds(),
                "executionTimeMs", aiResponse.getExecutionTimeMs()
        ));
    }
}