package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.roles.Role;
import ru.kata.spring.boot_security.demo.roles.User;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService service;

    public AdminController(UserService service) {
        this.service = service;
    }

    @GetMapping("")
    public String listUsers(Model model, Principal principal) {
        model.addAttribute("admin", service.findByUsername(principal.getName()));
        model.addAttribute("listUsers", service.listAll());
        model.addAttribute("listRoles", service.listRoles());
        model.addAttribute("newUser", new User());
        return "admin/admin";
    }

    @PostMapping("/create")
    public String addNewUser(@ModelAttribute("user") User user,
                             @RequestParam("nameRoles") ArrayList<Integer> roles) {
        service.saveUser(user, roles);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable("id") int id, Model model) {
        User user = service.get(id);
        List<Role> listRoles = service.listRoles();
        model.addAttribute("user", user);
        model.addAttribute("list1", listRoles);
        return "admin/user-update";
    }

    @PostMapping("/upd/{id}")
    public String updateUser(@ModelAttribute("user") User user,
            @PathVariable("id") int id,
                             @RequestParam("nameRoles2") ArrayList<Integer> roles
    ) {
//        user = service.get(id);
        service.saveUser(user, roles);
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}/user-delete")
    public String deleteUser(@PathVariable("id") int id) {
        service.deleteById(id);
        return "redirect:/admin";
    }


    @PostMapping("/update/{id}")
    public String updateUser(@ModelAttribute("user") User user, @RequestParam(value = "role") String role) {
        user.setRoles(service.findRolesByName(role));
        service.updateUser(user);
        return "redirect:/admin";
    }
}

