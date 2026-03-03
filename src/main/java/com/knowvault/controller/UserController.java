package com.knowvault.controller;

import com.knowvault.model.User;
import com.knowvault.model.dto.UserCreateForm;
import com.knowvault.model.dto.UserUpdateForm;
import com.knowvault.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // LIST
    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.listUsers());
        return "users/list";
    }

    // CREATE FORM
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new UserCreateForm());
        return "users/create";
    }

    // CREATE POST (PRG)
    @PostMapping
    public String create(@Valid @ModelAttribute("form") UserCreateForm form,
                        BindingResult bindingResult,
                        RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "users/create";
        }

        userService.createUser(form);
        ra.addFlashAttribute("success", "Usuario creado correctamente.");
        return "redirect:/users";
    }

    // EDIT FORM
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") int id, Model model) {
        User u = userService.getUserOrThrow(id);

        UserUpdateForm form = new UserUpdateForm();
        form.setUsername(u.getUsername());
        form.setEmail(u.getEmail());
        form.setRole(u.getRole());
        form.setPassword(""); // vacío por defecto

        model.addAttribute("userId", id);
        model.addAttribute("form", form);
        return "users/edit";
    }

    // UPDATE POST (PRG)
    @PostMapping("/{id}")
    public String update(@PathVariable("id") int id,
                        @Valid @ModelAttribute("form") UserUpdateForm form,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", id);
            return "users/edit";
        }

        userService.updateUser(id, form);
        ra.addFlashAttribute("success", "Usuario actualizado correctamente.");
        return "redirect:/users";
    }

    // DELETE POST (PRG)
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") int id, RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("success", "Usuario eliminado correctamente.");
        return "redirect:/users";
    }
}