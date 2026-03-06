package com.knowvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.knowvault.model.User;
import com.knowvault.model.dto.LoginForm;
import com.knowvault.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ViewController {

    private final UserService userService;

    public ViewController(UserService userService) {
        this.userService = userService;
    }

    // ==============================
    // LOGIN PAGE
    // ==============================

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {

        model.addAttribute("form", new LoginForm());

        return "auth/login";
    }

    // ==============================
    // LOGIN PROCESS
    // ==============================

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("form") LoginForm form,
                        BindingResult result,
                        HttpSession session,
                        Model model) {

        if (result.hasErrors()) {
            return "login";
        }

        User user = userService.login(form);

        if (user == null) {
            model.addAttribute("loginError", "Invalid email or password");
            return "login";
        }

        session.setAttribute("user", user);

        return "redirect:/dashboard";
    }

    // ==============================
    // DASHBOARD
    // ==============================

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        return "dashboard/index";
    }

    // ==============================
    // DOCUMENTS PAGE
    // ==============================

    @GetMapping("/documents")
    public String documents(HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        return "documents/list";
    }

    // ==============================
    // UPLOAD PAGE
    // ==============================

    @GetMapping("/upload")
    public String upload(HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        return "documents/upload";
    }

    // ==============================
    // ASK AI PAGE
    // ==============================

    @GetMapping("/ask")
    public String ask(HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        return "ai/ask";
    }

    // ==============================
    // HISTORY PAGE
    // ==============================

    @GetMapping("/history")
    public String history(HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        return "history/list";
    }

    // ==============================
    // LOGOUT
    // ==============================

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}