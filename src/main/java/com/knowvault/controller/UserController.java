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


    @GetMapping
    public String listUsers(Model model) {

        model.addAttribute("users", userService.getAllUsers());

        return "users/list";
    }


    @GetMapping("/create")
    public String showCreateForm(Model model) {

        model.addAttribute("form", new UserCreateForm());

        return "users/create";
    }


    @PostMapping
    public String createUser(
            @Valid @ModelAttribute("form") UserCreateForm form,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "users/create";
        }

        userService.createUser(form);

        return "redirect:/users";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        User user = userService.getUserById(id);

        UserUpdateForm form = new UserUpdateForm();

        form.setUserId(user.getUserId());
        form.setUsername(user.getUsername());
        form.setEmail(user.getEmail());
        form.setRole(user.getRole());

        model.addAttribute("form", form);

        return "users/edit";
    }


    @PostMapping("/edit/{id}")
    public String updateUser(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") UserUpdateForm form,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "users/edit";
        }

        userService.updateUser(id, form);

        return "redirect:/users";
    }


    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return "redirect:/users";
    }
}