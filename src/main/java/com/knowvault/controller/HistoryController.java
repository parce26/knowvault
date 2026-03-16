package com.knowvault.controller;

import com.knowvault.model.QueryHistory;
import com.knowvault.model.User;
import com.knowvault.service.QueryHistoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * HistoryController - Handles HTTP requests for the Query History module.
 * Routes: GET /history, POST /history/delete/{id}, POST /history/clear
 *
 * @author Kevin García Gutiérrez
 */
@Controller
@RequestMapping("/history")
public class HistoryController {

    private final QueryHistoryService queryHistoryService;

    public HistoryController(QueryHistoryService queryHistoryService) {
        this.queryHistoryService = queryHistoryService;
    }

    // ==============================
    // GET /history
    // List all history (with optional search)
    // ==============================

    @GetMapping
    public String listHistory(
            @RequestParam(required = false) String search,
            HttpSession session,
            Model model
    ) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("loggedUser");
        Long userId = user.getUserId();

        List<QueryHistory> entries = queryHistoryService.searchHistory(userId, search);

        model.addAttribute("user", user);
        model.addAttribute("historyEntries", entries);
        model.addAttribute("search", search);
        model.addAttribute("totalQueries", queryHistoryService.getTotalQueries(userId));

        return "history/list";
    }

    // ==============================
    // POST /history/delete/{id}
    // Delete a single history entry
    // ==============================

    @PostMapping("/delete/{id}")
    public String deleteEntry(
            @PathVariable Long id,
            HttpSession session
    ) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        queryHistoryService.deleteEntry(id);

        return "redirect:/history";
    }

    // ==============================
    // POST /history/clear
    // Delete ALL history for logged user
    // ==============================

    @PostMapping("/clear")
    public String clearHistory(HttpSession session) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("loggedUser");

        queryHistoryService.clearAllHistory(user.getUserId());

        return "redirect:/history";
    }
}