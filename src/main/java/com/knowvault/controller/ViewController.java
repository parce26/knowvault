package com.knowvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.knowvault.model.User;
import com.knowvault.model.dto.LoginForm;
import com.knowvault.service.DocumentService;
import com.knowvault.service.QueryHistoryService;
import com.knowvault.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ViewController {

    private final UserService userService;
    private final DocumentService documentService;
    private final QueryHistoryService queryHistoryService;

    public ViewController(
            UserService userService,
            DocumentService documentService,
            QueryHistoryService queryHistoryService
    ) {
        this.userService = userService;
        this.documentService = documentService;
        this.queryHistoryService = queryHistoryService;
    }

    // =========================================
    // LOGIN PAGE
    // =========================================

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("form", new LoginForm());
        return "auth/login";
    }

    // =========================================
    // LOGIN PROCESS
    // =========================================

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("form") LoginForm form,
            BindingResult bindingResult,
            HttpSession session,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        User user = userService.authenticate(form.getEmail(), form.getPassword());

        if (user == null) {
            model.addAttribute("loginError", "Invalid email or password");
            return "auth/login";
        }

        session.setAttribute("loggedUser", user);
        return "redirect:/dashboard";
    }

    // =========================================
    // LOGOUT
    // =========================================

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // =========================================
    // DASHBOARD
    // =========================================

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        if (!isLogged(session)) {
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("loggedUser");

        // Stats reales desde la base de datos
        int totalDocuments = documentService.getTotalDocuments();
        String storageUsed = documentService.formatStorageSize(
                documentService.getTotalStorageUsedBytes()
        );
        int totalQueries = queryHistoryService.getTotalQueries(user.getUserId());

        model.addAttribute("user", user);
        model.addAttribute("totalDocuments", totalDocuments);
        model.addAttribute("storageUsed", storageUsed);
        model.addAttribute("totalQueries", totalQueries);
        model.addAttribute("recentDocuments", documentService.getRecentDocuments(5));

        return "dashboard/index";
    }

    // =========================================
    // ASK AI PAGE
    // =========================================

    @GetMapping("/ask")
    public String askAI(HttpSession session, Model model) {

        if (!isLogged(session)) {
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("loggedUser");
        model.addAttribute("user", user);

        return "ai/ask";
    }

    // =========================================
    // SESSION CHECK
    // =========================================

    private boolean isLogged(HttpSession session) {
        return session.getAttribute("loggedUser") != null;
    }
}