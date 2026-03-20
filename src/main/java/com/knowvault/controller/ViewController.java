package com.knowvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.knowvault.model.User;
import com.knowvault.model.dto.LoginForm;
import com.knowvault.model.dto.RegisterForm;
import com.knowvault.model.dto.UserCreateForm;
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
    // LANDING PAGE
    // =========================================

    @GetMapping("/")
    public String landing(HttpSession session) {
        if (isLogged(session)) {
            return "redirect:/dashboard";
        }
        return "landing";
    }

    // =========================================
    // REGISTER PAGE
    // =========================================

    @GetMapping("/register")
    public String registerPage(HttpSession session, Model model) {
        if (isLogged(session)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("form", new RegisterForm());
        return "auth/register";
    }

    // =========================================
    // REGISTER PROCESS
    // =========================================

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("form") RegisterForm form,
            BindingResult bindingResult,
            HttpSession session,
            Model model
    ) {
        // Validation errors
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        // Passwords must match
        if (!form.passwordsMatch()) {
            model.addAttribute("registerError", "Passwords do not match");
            return "auth/register";
        }

        // Try to create the user
        try {
            UserCreateForm createForm = new UserCreateForm();
            createForm.setUsername(form.getUsername());
            createForm.setEmail(form.getEmail());
            createForm.setPassword(form.getPassword());
            createForm.setRole("user");

            userService.createUser(createForm);

        } catch (Exception e) {
            model.addAttribute("registerError", e.getMessage());
            return "auth/register";
        }

        // Auto-login after registration
        User user = userService.authenticate(form.getEmail(), form.getPassword());
        if (user != null) {
            session.setAttribute("loggedUser", user);
            return "redirect:/dashboard";
        }

        return "redirect:/login";
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
        return "redirect:/";
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