package com.knowvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.knowvault.model.User;
import com.knowvault.model.dto.UserCreateForm;
import com.knowvault.model.dto.UserUpdateForm;
import com.knowvault.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ==============================
    // LIST USERS
    // ==============================

    @GetMapping
    public String listUsers(Model model) {

        model.addAttribute("users", userService.getAllUsers());

        return "users/list";
    }

    // ==============================
    // CREATE USER FORM
    // ==============================

    @GetMapping("/create")
    public String createUserForm(Model model) {

        model.addAttribute("form", new UserCreateForm());

        return "users/create";
    }

    // ==============================
    // CREATE USER
    // ==============================

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("form") UserCreateForm form,
                             BindingResult result) {

        if (result.hasErrors()) {
            return "users/create";
        }

        userService.createUser(form);

        return "redirect:/users";
    }

    // ==============================
    // EDIT USER FORM
    // ==============================

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {

        User user = userService.getUserById(id);

        if (user == null) {
            return "redirect:/users";
        }

        UserUpdateForm form = new UserUpdateForm();
        form.setUserId(user.getUserId());
        form.setUsername(user.getUsername());
        form.setEmail(user.getEmail());

        model.addAttribute("form", form);

        return "users/edit";
    }

    // ==============================
    // UPDATE USER
    // ==============================

    @PostMapping("/edit")
    public String updateUser(@Valid @ModelAttribute("form") UserUpdateForm form,
                             BindingResult result) {

        if (result.hasErrors()) {
            return "users/edit";
        }

        userService.updateUser(form);

        return "redirect:/users";
    }

    // ==============================
    // DELETE USER
    // ==============================

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return "redirect:/users";
    }
}