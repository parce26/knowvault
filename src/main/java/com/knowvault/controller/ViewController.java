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
import com.knowvault.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ViewController {

    private final UserService userService;
    private final DocumentService documentService;

    public ViewController(
            UserService userService,
            DocumentService documentService
    ) {
        this.userService = userService;
        this.documentService = documentService;
    }

    /*
    =========================================
    LOGIN PAGE
    =========================================
     */

    @GetMapping("/login")
    public String loginPage(Model model) {

        model.addAttribute("form", new LoginForm());

        return "auth/login";
    }

    /*
    =========================================
    LOGIN PROCESS
    =========================================
     */

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

        User user = userService.authenticate(
                form.getEmail(),
                form.getPassword()
        );

        if (user == null) {

            model.addAttribute("loginError", "Invalid email or password");

            return "auth/login";
        }

        session.setAttribute("loggedUser", user);

        return "redirect:/dashboard";
    }

    /*
    =========================================
    LOGOUT
    =========================================
     */

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }

    /*
    =========================================
    DASHBOARD
    =========================================
     */

    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session,
            Model model
    ) {

        if (!isLogged(session)) {
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("loggedUser");

        model.addAttribute("user", user);

        model.addAttribute(
                "totalDocuments",
                documentService.searchDocuments(null).size()
        );

        model.addAttribute(
                "storageUsed",
                documentService.formatStorageSize(
                        documentService.getTotalStorageUsedBytes()
                )
        );

        model.addAttribute(
                "recentDocuments",
                documentService.getRecentDocuments(5)
        );

        return "dashboard/index";
    }



    /*
    =========================================
    UPLOAD PAGE
    =========================================
     */

    @GetMapping("/upload")
    public String upload(HttpSession session) {

        if (!isLogged(session)) {
            return "redirect:/login";
        }

        return "documents/upload";
    }

    /*
    =========================================
    AI PAGE
    =========================================
     */

    @GetMapping("/ask")
    public String askAI(HttpSession session) {

        if (!isLogged(session)) {
            return "redirect:/login";
        }

        return "ai/ask";
    }

    /*
    =========================================
    HISTORY PAGE
    =========================================
     */

    @GetMapping("/history")
    public String history(HttpSession session) {

        if (!isLogged(session)) {
            return "redirect:/login";
        }

        return "history/list";
    }

    /*
    =========================================
    SESSION CHECK
    =========================================
     */

    private boolean isLogged(HttpSession session) {

        return session.getAttribute("loggedUser") != null;

    }

}