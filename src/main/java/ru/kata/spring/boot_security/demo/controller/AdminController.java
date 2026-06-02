package ru.kata.spring.boot_security.demo.controller;

import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("allRoles", roleService.findAll());
        return "admin";
    }

    @GetMapping("/addUser")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.findAll());
        return "addUser";
    }

    @PostMapping("/addUser")
    public String addUser(@ModelAttribute User user,
                          @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            user.setRoles(new HashSet<>(roleService.findAllByIds(roleIds)));
        }
        userService.save(user);
        return "redirect:/admin";
    }

    @GetMapping("/editUser")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("allRoles", roleService.findAll());
        return "editUser";
    }

    @PostMapping("/editUser")
    public String editUser(@ModelAttribute User user,
                           @RequestParam(value = "roleIds", required = false) Set<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            user.setRoles(new HashSet<>(roleService.findAllByIds(roleIds)));
        }
        userService.update(user);
        return "redirect:/admin";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }

    // Добавление роли пользователю (работаем напрямую с User)
    @PostMapping("/user/{userId}/role/{roleId}/add")
    public String addRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        User user = userService.findById(userId);
        Role role = roleService.findById(roleId);
        if (user != null && role != null) {
            user.getRoles().add(role);
            userService.update(user);
        }
        return "redirect:/admin/editUser?id=" + userId;
    }

    // Удаление роли у пользователя
    @PostMapping("/user/{userId}/role/{roleId}/remove")
    public String removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        User user = userService.findById(userId);
        Role role = roleService.findById(roleId);
        if (user != null && role != null) {
            user.getRoles().remove(role);
            userService.update(user);
        }
        return "redirect:/admin/editUser?id=" + userId;
    }
}